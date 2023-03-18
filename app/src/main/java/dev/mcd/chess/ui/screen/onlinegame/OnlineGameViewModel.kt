package dev.mcd.chess.ui.screen.onlinegame

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bhlangonijr.chesslib.move.Move
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.game.TerminationReason
import dev.mcd.chess.feature.game.domain.GameSessionRepository
import dev.mcd.chess.online.domain.OnlineClientGameSession
import dev.mcd.chess.online.domain.usecase.FindGame
import dev.mcd.chess.online.domain.usecase.GetOrCreateUser
import dev.mcd.chess.online.domain.usecase.JoinOnlineGame
import dev.mcd.chess.online.domain.usecase.JoinOnlineGame.Event
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.SideEffect.AnnounceTermination
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapNotNull
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
    private val stateHandle: SavedStateHandle,
    private val joinOnlineGame: JoinOnlineGame,
    private val getOrCreateUser: GetOrCreateUser,
    private val findGame: FindGame,
) : ViewModel(), ContainerHost<OnlineGameViewModel.State, OnlineGameViewModel.SideEffect> {

    override val container = container<State, SideEffect>(
        initialState = State.FindingGame(),
    ) {
        viewModelScope.launch {
            gameSessionRepository.activeGame()
                .mapNotNull { it as? OnlineClientGameSession }
                .collectLatest { session ->
                    intent {
                        reduce {
                            State.InGame(
                                session = session,
                                terminated = false,
                            )
                        }
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

                clientSession()?.channel?.resign()

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
            val session = clientSession() ?: return@intent

            val terminated = state is State.InGame && (state as? State.InGame)?.terminated == true
            if (!terminated) {
                Timber.d("Moving for player: $move")
                session.move(move.toString())
                session.channel.move(move)
            }
        }
    }

    private fun findGame() {
        intent {
            runCatching {
                val userId = getOrCreateUser()
                reduce { State.FindingGame(userId) }

                Timber.d("Authenticated as $userId")

                val game = findGame.invoke()
                startGame(game.id)
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
            reduce {
                (state as? State.InGame)?.copy(terminated = true) ?: state
            }
            postSideEffect(AnnounceTermination(reason))
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
            val session: OnlineClientGameSession,
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
