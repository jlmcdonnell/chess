package dev.mcd.chess.ui.screen.botgame

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Constants
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.common.engine.ChessEngine
import dev.mcd.chess.common.game.ClientGameSession
import dev.mcd.chess.common.game.TerminationReason
import dev.mcd.chess.common.player.Bot
import dev.mcd.chess.common.player.HumanPlayer
import dev.mcd.chess.common.player.PlayerImage
import dev.mcd.chess.feature.game.domain.DefaultBots
import dev.mcd.chess.feature.game.domain.GameSessionRepository
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
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.math.max

@HiltViewModel
class BotGameViewModel @Inject constructor(
    private val engine: ChessEngine,
    private val gameSessionRepository: GameSessionRepository,
    private val state: SavedStateHandle,
) : ViewModel(), ContainerHost<BotGameViewModel.State, BotGameViewModel.SideEffect> {

    private lateinit var bot: Bot
    private lateinit var side: Side

    override val container = container<State, SideEffect>(State.Loading) {
        intent {
            repeatOnSubscription {
                engine.startAndWait()
            }
        }
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
        bot = DefaultBots.fromSlug(state.get<String>("bot")!!)
        side = Side.valueOf(state.get<String>("side")!!)
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
                endGame(game)
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
            val game = gameSessionRepository.activeGame().firstOrNull() ?: run {
                return@intent
            }
            if (game.move(move.toString())) {
                tryMoveBot(game)
            } else {
                endGame(game)
            }
        }
    }

    private suspend fun tryMoveBot(game: ClientGameSession) {
        val delayedMoveTime = System.currentTimeMillis() + (500 + (0..1000).random())
        val stockfishMoveSan = engine.getMove(game.fen(), level = bot.level, depth = bot.depth)
        delay(max(0, delayedMoveTime - System.currentTimeMillis()))
        game.move(stockfishMoveSan)

        if (game.termination() != null) {
            endGame(game)
        }
    }

    private fun startGame() {
        intent {
            engine.awaitReady()

            val board = Board().apply {
                clear()
                loadFromFen(Constants.startStandardFENPosition)
            }
            val game = ClientGameSession(
                id = UUID.randomUUID().toString(),
                self = HumanPlayer(
                    name = "You",
                    rating = 900,
                    image = PlayerImage.None
                ),
                selfSide = side,
                opponent = bot,
            )
            game.setBoard(board)
            gameSessionRepository.updateActiveGame(game)

            if (board.sideToMove != game.selfSide) {
                tryMoveBot(game)
            }
        }
    }

    private fun endGame(game: ClientGameSession) {
        intent {
            gameSessionRepository.updateActiveGame(null)

            reduce {
                (state as? State.Game)?.copy(terminated = true) ?: state
            }

            val termination = game.termination() ?: TerminationReason(resignation = side)

            postSideEffect(SideEffect.AnnounceTermination(termination))
        }
    }

    sealed interface State {
        object Loading : State

        data class Game(
            val game: ClientGameSession,
            val terminated: Boolean = false,
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
