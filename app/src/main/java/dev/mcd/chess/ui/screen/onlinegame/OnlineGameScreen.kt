package dev.mcd.chess.ui.screen.onlinegame

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import dev.mcd.chess.domain.game.TerminationReason
import dev.mcd.chess.ui.game.ActiveGameView
import dev.mcd.chess.ui.game.GameTermination
import dev.mcd.chess.ui.game.ResignationDialog
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.SideEffect.AnnounceTermination
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.SideEffect.ConfirmResignation
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.SideEffect.NavigateBack
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.State.FatalError
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.State.Game
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.State.Loading
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun OnlineGameScreen(
    viewModel: OnlineGameViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            val state by viewModel.collectAsState()
            var showTerminationReason by remember { mutableStateOf<TerminationReason?>(null) }
            var showResignation by remember { mutableStateOf<ConfirmResignation?>(null) }

            if (state is Game && (state as? Game)?.terminated == false) {
                BackHandler {
                    viewModel.onResign(andNavigateBack = true)
                }
            }

            viewModel.collectSideEffect { effect ->
                when (effect) {
                    is AnnounceTermination -> showTerminationReason = effect.reason
                    is ConfirmResignation -> showResignation = effect
                    is NavigateBack -> navigateBack()
                }
            }

            showTerminationReason?.let { reason ->
                GameTermination(
                    reason = reason,
                    onRestart = { viewModel.onRestart() },
                    onDismiss = { showTerminationReason = null }
                )
            }

            showResignation?.let { effect ->
                ResignationDialog(
                    onConfirm = {
                        effect.onConfirm()
                        showResignation = null
                    },
                    onDismiss = {
                        effect.onDismiss
                        showResignation = null
                    },
                )
            }

            when (val s = state) {
                is Game -> ActiveGameView(
                    game = s.game,
                    onMove = viewModel::onPlayerMove,
                    onResign = viewModel::onResign,
                    terminated = s.terminated,
                )

                is Loading -> Text(text = "Finding game ...")
                is FatalError -> Text(
                    text = """
                        A VERY BAD HAPPENED
                        
                        ${s.message}
                    """,
                    color = Color.Red
                )
            }
        }
    }
}
