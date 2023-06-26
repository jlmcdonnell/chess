package dev.mcd.chess.ui.screen.onlinegame

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.game.MoveResult
import dev.mcd.chess.common.game.TerminationReason
import dev.mcd.chess.feature.common.domain.AppPreferences
import dev.mcd.chess.feature.game.domain.GameSessionRepository
import dev.mcd.chess.feature.sound.domain.GameSessionSoundWrapper
import dev.mcd.chess.feature.sound.domain.SoundSettings
import dev.mcd.chess.online.domain.OnlineGameSession
import dev.mcd.chess.online.domain.usecase.FindGame
import dev.mcd.chess.online.domain.usecase.GetOrCreateUser
import dev.mcd.chess.online.domain.usecase.JoinOnlineGame
import dev.mcd.chess.online.domain.usecase.JoinOnlineGame.Event
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.SideEffect.AnnounceTermination
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class OnlineGameViewModel @Inject constructor(
    private val gameSessionRepository: GameSessionRepository,
    private val stateHandle: SavedStateHandle,
    private val joinOnlineGame: JoinOnlineGame,
    private val getOrCreateUser: GetOrCreateUser,
    private val findGame: FindGame,
    private val soundWrapper: GameSessionSoundWrapper,
    private val appPreferences: AppPreferences,
) : ViewModel(), ContainerHost<OnlineGameViewModel.State, OnlineGameViewModel.SideEffect> {

    override val container = container<State, SideEffect>(
        initialState = State.FindingGame(),
    ) {
        viewModelScope.launch {
            gameSessionRepository.activeGame()
                .mapNotNull { it as? OnlineGameSession }
                .collectLatest { session ->
                    intent {
                        reduce {
                            State.InGame(session = session)
                        }
                    }
                    intent {
                        val settings = SoundSettings(
                            enabled = appPreferences.soundsEnabled(),
                            enableNotify = true,
                        )
                        soundWrapper.attachSession(session, settings)
                    }
                }
        }
        val gameId = stateHandle.get<String>("gameId")
        if (gameId != null) {
            intent {
                runCatching {
                    startGame(gameId)
                }.onFailure {
                    Timber.e(it, "Retrieving game $gameId")
                    fatalError("Unable to retrieve existing game")
                }
            }
        } else {
            findGame()
        }
    }

    fun onRestart() {
        findGame()
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
                if (move(move.toString()) == MoveResult.Moved) {
                    clientSession()?.channel?.move(move)
                } else {
                    Timber.e("Illegal Move: $move")
                }
            }
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

    private fun findGame() {
        intent {
            runCatching {
                val userId = getOrCreateUser()
                reduce { State.FindingGame(userId) }

                Timber.d("Authenticated as $userId")

                val id = findGame.invoke()
                startGame(id)
            }.onFailure {
                Timber.e(it, "findingGame")
            }
        }
    }

    private fun startGame(id: GameId) {
        intent {
            runCatching {
                joinOnlineGame(id).collectLatest { event ->
                    when (event) {
                        is Event.FatalError -> fatalError(event.message)
                        is Event.Termination -> handleTermination(event.reason)
                        is Event.NewSession -> gameSessionRepository.updateActiveGame(event.session)
                    }
                }
            }.onFailure {
                fatalError("joining game", it)
            }
        }
    }

    private fun handleTermination(reason: TerminationReason) {
        intent {
            postSideEffect(
                AnnounceTermination(
                    sideMated = reason.sideMated,
                    draw = reason.draw,
                    resignation = reason.resignation,
                ),
            )
        }
    }

    private fun fatalError(message: String, throwable: Throwable? = null) {
        Timber.e(throwable, message)
        intent {
            reduce { State.FatalError(message) }
        }
    }

    private fun clientSession() = (container.stateFlow.value as? State.InGame)?.session

    sealed interface State {
        data class InGame(
            val session: OnlineGameSession,
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

        @Stable
        data class AnnounceTermination(
            val sideMated: Side? = null,
            val draw: Boolean = false,
            val resignation: Side? = null,
        ) : SideEffect

        object NavigateBack : SideEffect
    }
}
