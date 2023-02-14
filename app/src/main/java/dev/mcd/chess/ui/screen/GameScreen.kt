package dev.mcd.chess.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.mcd.chess.domain.model.TerminationReason
import dev.mcd.chess.ui.game.ActiveGameView
import dev.mcd.chess.ui.game.GameTerminationDialog
import dev.mcd.chess.ui.screen.GameScreenViewModel.SideEffect.AnnounceTermination
import dev.mcd.chess.ui.screen.GameScreenViewModel.State.Game
import dev.mcd.chess.ui.screen.GameScreenViewModel.State.Loading
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun GameScreen(
    viewModel: GameScreenViewModel = hiltViewModel(),
) {
    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            val state by viewModel.collectAsState()
            var showTerminationReason by remember { mutableStateOf<TerminationReason?>(null) }

            viewModel.collectSideEffect { effect ->
                when (effect) {
                    is AnnounceTermination -> showTerminationReason = effect.reason
                }
            }

            when (val s = state) {
                is Game -> ActiveGameView(
                    game = s.game,
                    onMove = viewModel::onPlayerMove,
                    onResign = viewModel::onResign,
                    terminated = s.terminated,
                )
                is Loading -> Unit
            }

            showTerminationReason?.let { reason ->
                GameTerminationDialog(reason) {
                    showTerminationReason = null
                }
            }
        }
    }
}
