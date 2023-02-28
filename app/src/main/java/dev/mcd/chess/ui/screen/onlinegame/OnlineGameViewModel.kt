package dev.mcd.chess.ui.screen.onlinegame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.domain.api.ChessApi
import dev.mcd.chess.domain.api.SessionInfo
import dev.mcd.chess.domain.api.opponent
import dev.mcd.chess.domain.api.sideForUser
import dev.mcd.chess.domain.game.BoardState
import dev.mcd.chess.domain.game.GameMessage
import dev.mcd.chess.domain.game.GameMessage.ErrorGameTerminated
import dev.mcd.chess.domain.game.GameMessage.ErrorNotUsersMove
import dev.mcd.chess.domain.game.GameMessage.SessionInfoMessage
import dev.mcd.chess.domain.game.GameSessionRepository
import dev.mcd.chess.domain.game.LocalGameSession
import dev.mcd.chess.domain.game.State
import dev.mcd.chess.domain.game.State.BLACK_CHECKMATED
import dev.mcd.chess.domain.game.State.BLACK_RESIGNED
import dev.mcd.chess.domain.game.State.DRAW
import dev.mcd.chess.domain.game.State.STARTED
import dev.mcd.chess.domain.game.State.WHITE_CHECKMATED
import dev.mcd.chess.domain.game.State.WHITE_RESIGNED
import dev.mcd.chess.domain.game.TerminationReason
import dev.mcd.chess.domain.player.HumanPlayer
import dev.mcd.chess.domain.player.PlayerImage
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.SideEffect.AnnounceTermination
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class OnlineGameViewModel @Inject constructor(
    private val gameSessionRepository: GameSessionRepository,
    private val chessApi: ChessApi,
) : ViewModel(), ContainerHost<OnlineGameViewModel.State, OnlineGameViewModel.SideEffect> {

    private lateinit var commandChannel: SendChannel<String>

    override val container = container<State, SideEffect>(
        initialState = State.FindingGame(),
    ) {
        viewModelScope.launch {
            gameSessionRepository.activeGame()
                .filterNotNull()
                .collectLatest { session ->
                    intent {
                        reduce {
                            State.Game(
                                game = session,
                                terminated = false,
                            )
                        }
                    }
                }
        }
        startGame()
    }

    fun onRestart() {
        startGame()
    }

    fun onResign(andNavigateBack: Boolean = false) {
        intent {
            runCatching {
                suspendCancellableCoroutine { continuation ->
                    intent {
                        postSideEffect(
                            SideEffect.ConfirmResignation(
                                onConfirm = { continuation.resume(Unit) },
                                onDismiss = { continuation.cancel() }
                            )
                        )
                    }
                }
                println("Resigning")
                commandChannel.send("resign")
                if (andNavigateBack) {
                    postSideEffect(SideEffect.NavigateBack)
                }
            }.onFailure {
                Timber.d("Will not resign")
            }
        }
    }

    fun onPlayerMove(move: Move) {
        intent {
            val game = gameSessionRepository.activeGame().firstOrNull() ?: return@intent

            val board = game.board
            val terminated = state is State.Game && (state as? State.Game)?.terminated == true
            if (!terminated && board.sideToMove == game.selfSide && move in board.legalMoves()) {
                Timber.d("Moving for player: $move")
                board.doMove(move)
                commandChannel.send(move.toString())
            }
        }
    }

    private fun startGame() {
        intent {
            runCatching {
                val userId = chessApi.userId() ?: chessApi.generateId()
                reduce { State.FindingGame(userId) }

                Timber.d("Authenticated as $userId")

                val remoteSession = chessApi.findGame()

//                chessApi.storeToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJZb3UuIiwiaXNzIjoiY2hlc3MubWNkLmRldiIsImp0aSI6InVzZXIxIn0.Ii62vyWQIhAxsDQSdc0pAAnPed6PuKQJ5SI-ae9ClvBgTWG2ZfHMwGms8jxVVxBySekh9r3rirR_Npvv4vMKyA")
//                val userId = "user1"
//                val remoteSession = chessApi.session("debug")

                val board = Board().apply {
                    loadFromFen(remoteSession.board.fen)
                    moveCounter = remoteSession.board.moveCount
                }

                val session = LocalGameSession(
                    id = remoteSession.sessionId,
                    board = board,
                    self = HumanPlayer(
                        name = userId,
                        image = PlayerImage.None,
                        rating = 0,
                    ),
                    opponent = HumanPlayer(
                        name = remoteSession.opponent(userId),
                        image = PlayerImage.None,
                        rating = 0,
                    ),
                    selfSide = remoteSession.sideForUser(userId)
                )
                gameSessionRepository.updateActiveGame(session)


                chessApi.joinGame(remoteSession.sessionId) {
                    println("In Game!")
                    commandChannel = outgoing

                    for (message in incoming) {
                        println("Received message: ${message::class}")
                        when (message) {
                            is SessionInfoMessage -> handleSessionState(session, message.sessionInfo)
                            is ErrorNotUsersMove -> Timber.e("ErrorNotUsersMove")
                            is ErrorGameTerminated -> Timber.e("ErrorGameTerminated")
                        }
                    }
                }
            }.onFailure {
                fatalError("starting game: $it", it)
            }
        }
    }

    private fun fatalError(message: String, throwable: Throwable? = null) {
        Timber.e(throwable, message)
        intent {
            reduce { State.FatalError(message) }
        }
    }

    private fun handleSessionState(session: LocalGameSession, sessionInfo: SessionInfo) {
        intent {
            when (sessionInfo.state) {
                STARTED -> Unit
                DRAW -> postSideEffect(AnnounceTermination(TerminationReason(draw = true)))
                WHITE_RESIGNED -> postSideEffect(AnnounceTermination(TerminationReason(resignation = Side.WHITE)))
                BLACK_RESIGNED -> postSideEffect(AnnounceTermination(TerminationReason(resignation = Side.BLACK)))
                WHITE_CHECKMATED -> postSideEffect(AnnounceTermination(TerminationReason(sideMated = Side.WHITE)))
                BLACK_CHECKMATED -> postSideEffect(AnnounceTermination(TerminationReason(sideMated = Side.BLACK)))
            }
            reduce { (state as? State.Game)?.copy(terminated = sessionInfo.state != STARTED) ?: state }

            updateBoardState(session, sessionInfo.board)
        }
    }

    private fun updateBoardState(session: LocalGameSession, boardState: BoardState) {
        val localBoard = session.board
        if (localBoard.fen == boardState.fen) {
            Timber.d("FEN equals remote FEN (last move=${boardState.lastMoveSan} fen=${boardState.fen})")
        } else if (boardState.lastMoveSide != null && boardState.lastMoveSan != null) {
            Timber.d("Our FEN:\n\t${localBoard.fen}\nTheir FEN:\n\t${boardState.fen}")
            val move = Move(boardState.lastMoveSan, boardState.lastMoveSide)
            if (move in localBoard.legalMoves()) {
                localBoard.doMove(move)
            } else {
                Timber.e("Cannot make move ($move)")
            }
        }
    }

    sealed interface State {
        data class Game(
            val game: LocalGameSession,
            val terminated: Boolean = false,
        ) : State

        data class FindingGame(
            val username: String? = null,
        ) : State

        data class FatalError(
            val message: String,
        ) : State
    }

    sealed interface SideEffect {
        data class ConfirmResignation(
            val onConfirm: () -> Unit,
            val onDismiss: () -> Unit,
        ) : SideEffect

        data class AnnounceTermination(
            val reason: TerminationReason,
        ) : SideEffect

        object NavigateBack : SideEffect
    }
}
