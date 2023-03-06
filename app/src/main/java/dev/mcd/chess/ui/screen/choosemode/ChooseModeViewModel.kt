package dev.mcd.chess.ui.screen.choosemode

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.domain.api.ChessApi
import dev.mcd.chess.domain.game.GameId
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChooseModeViewModel @Inject constructor(
    private val chessApi: ChessApi,
) : ViewModel(), ContainerHost<ChooseModeViewModel.State, ChooseModeViewModel.SideEffect> {

    override val container = container<State, SideEffect>(State()) {
        intent {
            repeatOnSubscription {
                runCatching {
                    val existingGame = chessApi.gameForUser().map { it.id }.firstOrNull()
                    if (existingGame != null) {
                        postSideEffect(SideEffect.NavigateToExistingGame(existingGame))
                    }
                }.onFailure {
                    Timber.e(it, "Finding existing games")
                }

                while (true) {
                    runCatching {
                        val lobbyInfo = chessApi.lobbyInfo()
                        reduce { state.copy(inLobby = lobbyInfo.inLobby) }
                    }.onFailure {
                        Timber.e(it, "Getting lobby info")
                    }
                    delay(2000)
                }
            }
        }
    }

    data class State(val inLobby: Int? = null)

    sealed interface SideEffect {
        data class NavigateToExistingGame(val id: GameId) : SideEffect
    }
}
