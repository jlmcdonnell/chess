package dev.mcd.chess.ui.game.board.chessboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.common.game.BoardSounds
import dev.mcd.chess.common.game.local.GameSessionRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ChessBoardViewModel @Inject constructor(
    private val boardSounds: BoardSounds,
    private val sessionRepository: GameSessionRepository,
) : ViewModel(), ContainerHost<ChessBoardViewModel.State, ChessBoardViewModel.SideEffect> {

    override val container = container<State, SideEffect>(State) {
        viewModelScope.launch {
            sessionRepository.activeGame()
                .filterNotNull()
                .collectLatest {
                    boardSounds.notify()
                    boardSounds.awaitMoves(it)
                }
        }
    }

    object State

    sealed interface SideEffect
}
