package dev.mcd.chess.ui.screen.botgame

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dev.mcd.chess.common.game.TerminationReason
import dev.mcd.chess.ui.compose.StableHolder
import dev.mcd.chess.ui.game.GameTermination
import dev.mcd.chess.ui.game.GameView
import dev.mcd.chess.ui.game.ResignationDialog
import dev.mcd.chess.ui.screen.botgame.BotGameViewModel.SideEffect.AnnounceTermination
import dev.mcd.chess.ui.screen.botgame.BotGameViewModel.SideEffect.ConfirmResignation
import dev.mcd.chess.ui.screen.botgame.BotGameViewModel.SideEffect.NavigateBack
import dev.mcd.chess.ui.screen.botgame.BotGameViewModel.State.Game
import dev.mcd.chess.ui.screen.botgame.BotGameViewModel.State.Loading
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun BotGameScreen(
    viewModel: BotGameViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {
            val state by viewModel.collectAsState()
            var showTermination by remember { mutableStateOf<AnnounceTermination?>(null) }
            var showResignation by remember { mutableStateOf<ConfirmResignation?>(null) }

            BackHandler {
                viewModel.onResign(andNavigateBack = true)
            }

            viewModel.collectSideEffect { effect ->
                when (effect) {
                    is AnnounceTermination -> showTermination = effect
                    is ConfirmResignation -> showResignation = effect
                    is NavigateBack -> navigateBack()
                }
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
                is Game -> GameView(
                    gameHolder = s.gameHolder,
                    onMove = viewModel::onPlayerMove,
                    onResign = viewModel::onResign,
                )

                is Loading -> Unit
            }

            showTermination?.let { (sideMated, draw, resignation) ->
                GameTermination(
                    sideMated = sideMated,
                    draw = draw,
                    resignation = resignation,
                    onRestart = { viewModel.onRestart() },
                    onDismiss = { showTermination = null },
                )
            }
        }
    }
}
