package dev.mcd.chess.ui.screen.botgame

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Constants
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.data.stockfish.StockfishAdapter
import dev.mcd.chess.domain.bot.Bot
import dev.mcd.chess.domain.bot.botFromSlug
import dev.mcd.chess.domain.game.TerminationReason
import dev.mcd.chess.domain.game.bot.BotRemoteGameChannel
import dev.mcd.chess.domain.game.local.ClientGameSession
import dev.mcd.chess.domain.game.local.GameSessionRepository
import dev.mcd.chess.domain.player.HumanPlayer
import dev.mcd.chess.domain.player.PlayerImage
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
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class BotGameViewModel @Inject constructor(
    private val stockfish: StockfishAdapter,
    private val gameSessionRepository: GameSessionRepository,
    private val state: SavedStateHandle,
) : ViewModel(), ContainerHost<BotGameViewModel.State, BotGameViewModel.SideEffect> {

    private lateinit var bot: Bot
    private lateinit var side: Side

    override val container = container<State, SideEffect>(State.Loading) {
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
        bot = state.get<String>("bot")!!.botFromSlug()
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
            val game = gameSessionRepository.activeGame().firstOrNull() ?: return@intent
            if (game.doMove(move.toString())) {
                tryMoveBot(game)
            } else {
                endGame(game)
            }
        }
    }

    private suspend fun tryMoveBot(game: ClientGameSession) {
        Timber.d("Moving for stockfish")
        val delayedMoveTime = System.currentTimeMillis() + (500 + (0..1000).random())
        val stockfishMoveSan = stockfish.getMove(game.fen(), level = bot.level, depth = bot.depth)
        val delay = delayedMoveTime - System.currentTimeMillis()
        if (delay > 0) {
            delay(delay)
        }

        game.doMove(stockfishMoveSan)

        if (game.termination() != null) {
            endGame(game)
        }
    }

    private fun startGame() {
        intent {
            val board = Board().apply {
                clear()

                /*
                    For Debugging:
                        Promotion      8/8/8/8/2k3p1/8/7p/2K5 w - - 0 1
                        Castling       r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 1 1
                        En Passant     rnbqkbnr/ppp1pppp/8/8/3pP3/P6P/1PPP1PP1/RNBQKBNR b KQkq e3 0 3
                */
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
                channel = BotRemoteGameChannel()
            )
            game.setBoard(Board())
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
