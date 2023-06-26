package dev.mcd.chess.ui.screen.botgame

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.common.game.MoveResult
import dev.mcd.chess.common.game.TerminationReason
import dev.mcd.chess.common.player.Bot
import dev.mcd.chess.engine.lc0.MaiaWeights
import dev.mcd.chess.feature.common.domain.AppPreferences
import dev.mcd.chess.feature.engine.BotEngineProxy
import dev.mcd.chess.feature.game.domain.DefaultBots
import dev.mcd.chess.feature.game.domain.GameSessionRepository
import dev.mcd.chess.feature.game.domain.usecase.MoveForBot
import dev.mcd.chess.feature.game.domain.usecase.StartBotGame
import dev.mcd.chess.feature.sound.domain.GameSessionSoundWrapper
import dev.mcd.chess.feature.sound.domain.SoundSettings
import dev.mcd.chess.ui.compose.StableHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class BotGameViewModel @Inject constructor(
    private val engine: BotEngineProxy,
    private val gameSessionRepository: GameSessionRepository,
    private val state: SavedStateHandle,
    private val startBotGame: StartBotGame,
    private val moveForBot: MoveForBot,
    private val soundWrapper: GameSessionSoundWrapper,
    private val appPreferences: AppPreferences,
) : ViewModel(), ContainerHost<BotGameViewModel.State, BotGameViewModel.SideEffect> {

    private lateinit var bot: Bot
    private lateinit var side: Side

    override val container = container<State, SideEffect>(State.Loading) {
        intent {
            repeatOnSubscription {
                engine.start(MaiaWeights.valueOf(bot.slug))
                try {
                    awaitCancellation()
                } finally {
                    engine.stop()
                }
            }
        }
        viewModelScope.launch {
            gameSessionRepository.activeGame()
                .filterNotNull()
                .collectLatest { game ->
                    intent {
                        reduce {
                            State.Game(StableHolder(game))
                        }
                    }
                    intent {
                        handleTermination(game.awaitTermination())
                    }
                    intent {
                        val soundSettings = SoundSettings(
                            enableNotify = true,
                            enabled = appPreferences.soundsEnabled(),
                        )
                        soundWrapper.attachSession(game, soundSettings)
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
            gameSessionRepository.activeGame().firstOrNull()?.run {
                if (confirmResignation()) {
                    resign()
                }
            }
            if (andNavigateBack) {
                postSideEffect(SideEffect.NavigateBack)
            }
        }
    }

    fun onPlayerMove(move: Move) {
        intent {
            gameSessionRepository.activeGame().firstOrNull()?.run {
                if (isSelfTurn() && move(move.toString()) == MoveResult.Moved) {
                    CoroutineScope(Dispatchers.Default).launch {
                        runCatching {
                            moveForBot()
                        }.onFailure {
                            Timber.e(it, "Moving for bot")
                        }
                    }
                } else {
                    Timber.e("Illegal Move: $move")
                }
            }
        }
    }

    private fun startGame() {
        intent {
            startBotGame(side, bot)
        }
    }

    private fun handleTermination(reason: TerminationReason) {
        intent {
            gameSessionRepository.updateActiveGame(null)
            postSideEffect(
                SideEffect.AnnounceTermination(
                    sideMated = reason.sideMated,
                    draw = reason.draw,
                    resignation = reason.resignation,
                ),
            )
        }
    }

    private suspend fun confirmResignation(): Boolean {
        return suspendCoroutine { continuation ->
            intent {
                postSideEffect(
                    SideEffect.ConfirmResignation(
                        onConfirm = { continuation.resume(true) },
                        onDismiss = { continuation.resume(false) },
                    ),
                )
            }
        }
    }

    sealed interface State {
        object Loading : State

        data class Game(
            val gameHolder: StableHolder<GameSession>,
        ) : State
    }

    sealed interface SideEffect {
        data class ConfirmResignation(
            val onConfirm: () -> Unit,
            val onDismiss: () -> Unit,
        ) : SideEffect

        @Stable
        data class AnnounceTermination(
            val sideMated: Side? = null,
            val draw: Boolean = false,
            val resignation: Side? = null,
        ) : SideEffect

        object NavigateBack : SideEffect
    }
}
