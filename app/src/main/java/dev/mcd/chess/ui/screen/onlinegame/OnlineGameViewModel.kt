package dev.mcd.chess.ui.screen.onlinegame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.move.Move
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.domain.api.ChessApi
import dev.mcd.chess.domain.api.opponent
import dev.mcd.chess.domain.api.sideForUser
import dev.mcd.chess.domain.game.BoardState
import dev.mcd.chess.domain.game.GameMessage.BoardStateMessage
import dev.mcd.chess.domain.game.GameMessage.ErrorNotUsersMove
import dev.mcd.chess.domain.game.GameMessage.GameTermination
import dev.mcd.chess.domain.game.GameSessionRepository
import dev.mcd.chess.domain.game.LocalGameSession
import dev.mcd.chess.domain.game.TerminationReason
import dev.mcd.chess.domain.player.HumanPlayer
import dev.mcd.chess.domain.player.PlayerImage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
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
import kotlin.math.abs

@HiltViewModel
class OnlineGameViewModel @Inject constructor(
    private val gameSessionRepository: GameSessionRepository,
    private val chessApi: ChessApi,
) : ViewModel(), ContainerHost<OnlineGameViewModel.State, OnlineGameViewModel.SideEffect> {

    private lateinit var moveChannel: SendChannel<String>

    override val container = container<State, SideEffect>(
        initialState = State.Loading,
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
            val game = gameSessionRepository.activeGame().firstOrNull() ?: return@intent
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
                endGame(game, endedByRemote = false)
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
            if (board.sideToMove == game.selfSide && move in board.legalMoves()) {
                Timber.d("Moving for player")
                board.doMove(move)
                moveChannel.send(move.toString())
            }
        }
    }

    private fun startGame() {
        intent {
            runCatching {
//                val userId = chessApi.userId() ?: chessApi.generateId()
//                val remoteSession = chessApi.findGame()

                chessApi.storeToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJhdWQiOiJZb3UuIiwiaXNzIjoiY2hlc3MubWNkLmRldiIsImp0aSI6InVzZXIxIn0.Ii62vyWQIhAxsDQSdc0pAAnPed6PuKQJ5SI-ae9ClvBgTWG2ZfHMwGms8jxVVxBySekh9r3rirR_Npvv4vMKyA")
                val userId = "user1"
                val remoteSession = chessApi.session("debug")

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
                    val moveChan = Channel<String>(1)
                    moveChannel = moveChan

                    for (message in incoming) {
                        println("Received message: ${message::class}")
                        when (message) {
                            is BoardStateMessage -> {
                                updateBoardState(session, message.state)
                            }

                            is ErrorNotUsersMove -> Timber.e("ErrorNotUsersMove")
                            is GameTermination -> endGame(session, endedByRemote = true)
                        }


                        if (message is BoardStateMessage && board.sideToMove == session.selfSide) {
                            println("Waiting move")
                            send(moveChan.receive())
                            println("Sent move")
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

    private fun updateBoardState(session: LocalGameSession, boardState: BoardState) {
        val localBoard = session.board
        val moveDiff = abs(boardState.moveCount - localBoard.moveCounter)
        if (moveDiff == 0 && localBoard.fen == boardState.fen) {
            println("Nothing to update")
        } else if (moveDiff < 2) {
            val lastMoveSan = boardState.lastMoveSan
            val lastMoveSide = boardState.lastMoveSide

            if (lastMoveSan != null && lastMoveSide != null) {
                val move = Move(lastMoveSan, lastMoveSide)
                localBoard.doMove(move)
            } else {
                fatalError("Illegal board state: lastMoveSan: $lastMoveSan lastMoveSide: $lastMoveSide")
            }
        } else {
            fatalError("Illegal board state:\nMove Diff $moveDiff\nOur FEN:\n\t${localBoard.fen}\nTheir FEN:\n\t${boardState.fen}")
        }
    }

    private fun endGame(game: LocalGameSession, endedByRemote: Boolean) {
        intent {
            val board = game.board
            gameSessionRepository.updateActiveGame(null)

            val mated = board.sideToMove.takeIf { board.isMated }
            val draw = board.isDraw
            val resignation =
                if (!endedByRemote && mated == null && !draw) board.sideToMove else null

            reduce {
                (state as? State.Game)?.copy(terminated = true) ?: state
            }

            val reason = TerminationReason(
                sideMated = mated,
                draw = draw,
                resignation = resignation,
            )
            postSideEffect(SideEffect.AnnounceTermination(reason))
        }
    }

    sealed interface State {
        object Loading : State

        data class Game(
            val game: LocalGameSession,
            val terminated: Boolean = false,
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
