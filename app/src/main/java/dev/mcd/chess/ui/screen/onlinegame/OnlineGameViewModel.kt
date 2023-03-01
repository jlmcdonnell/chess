package dev.mcd.chess.ui.screen.onlinegame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.domain.api.ActiveGame
import dev.mcd.chess.domain.api.ChessApi
import dev.mcd.chess.domain.api.MoveHistory
import dev.mcd.chess.domain.api.opponent
import dev.mcd.chess.domain.api.sideForUser
import dev.mcd.chess.domain.game.GameMessage.ErrorGameTerminated
import dev.mcd.chess.domain.game.GameMessage.ErrorNotUsersMove
import dev.mcd.chess.domain.game.GameMessage.MoveHistoryMessage
import dev.mcd.chess.domain.game.GameMessage.MoveMessage
import dev.mcd.chess.domain.game.GameMessage.SessionInfoMessage
import dev.mcd.chess.domain.game.GameSessionRepository
import dev.mcd.chess.domain.game.GameState
import dev.mcd.chess.domain.game.GameState.BLACK_CHECKMATED
import dev.mcd.chess.domain.game.GameState.BLACK_RESIGNED
import dev.mcd.chess.domain.game.GameState.DRAW
import dev.mcd.chess.domain.game.GameState.STARTED
import dev.mcd.chess.domain.game.GameState.WHITE_CHECKMATED
import dev.mcd.chess.domain.game.GameState.WHITE_RESIGNED
import dev.mcd.chess.domain.game.LocalGameSession
import dev.mcd.chess.domain.game.TerminationReason
import dev.mcd.chess.domain.player.HumanPlayer
import dev.mcd.chess.domain.player.PlayerImage
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.SideEffect.AnnounceTermination
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

    private var activeGame: ActiveGame? = null

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

                activeGame?.resign()
                if (andNavigateBack) {
                    postSideEffect(SideEffect.NavigateBack)
                }
            }.onFailure {
                Timber.e("onResign", it)
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
                activeGame?.move(move)
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

                val board = Board()
                val session = LocalGameSession(
                    id = remoteSession.sessionId,
                    board = board,
                    initialBitboard = board.bitboard,
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
                    activeGame = this
                    Timber.d("Joined game ${session.id}")

                    requestMoveHistory()

                    for (message in incoming) {
                        Timber.d("Received message: ${message::class.simpleName}")
                        when (message) {
                            is SessionInfoMessage -> handleSessionState(message.sessionInfo.state)
                            is MoveHistoryMessage -> resetBoard(session, message.moveHistory)
                            is MoveMessage -> addRemoteMove(session, message.move)
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

    private suspend fun resetBoard(session: LocalGameSession, moveHistory: MoveHistory) {
        val board = Board()
        moveHistory.moveList.forEach { board.doMove(it) }
        gameSessionRepository.updateActiveGame(
            game = session.copy(
                board = board,
            )
        )
    }

    private fun handleSessionState(gameState: GameState) {
        intent {
            when (gameState) {
                STARTED -> Unit
                DRAW -> postSideEffect(AnnounceTermination(TerminationReason(draw = true)))
                WHITE_RESIGNED -> postSideEffect(AnnounceTermination(TerminationReason(resignation = Side.WHITE)))
                BLACK_RESIGNED -> postSideEffect(AnnounceTermination(TerminationReason(resignation = Side.BLACK)))
                WHITE_CHECKMATED -> postSideEffect(AnnounceTermination(TerminationReason(sideMated = Side.WHITE)))
                BLACK_CHECKMATED -> postSideEffect(AnnounceTermination(TerminationReason(sideMated = Side.BLACK)))
            }
            reduce { (state as? State.Game)?.copy(terminated = gameState != STARTED) ?: state }
        }
    }

    private fun addRemoteMove(session: LocalGameSession, move: String) {
        val fen = session.board.fen
        val success = runCatching { session.board.doMove(move) }.getOrElse { false }
        if (!success) {
            Timber.e("Board is out of sync: $fen\nUnable to make move $move")
            intent {
                activeGame?.requestMoveHistory()
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
