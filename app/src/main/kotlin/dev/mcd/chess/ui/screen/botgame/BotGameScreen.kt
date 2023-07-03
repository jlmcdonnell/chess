package dev.mcd.chess.ui.screen.botgame

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import dev.mcd.chess.R
import dev.mcd.chess.ui.game.GameTermination
import dev.mcd.chess.ui.game.GameView
import dev.mcd.chess.ui.game.ResignationDialog
import dev.mcd.chess.ui.screen.botgame.BotGameViewModel.SideEffect.AnnounceTermination
import dev.mcd.chess.ui.screen.botgame.BotGameViewModel.SideEffect.ConfirmResignation
import dev.mcd.chess.ui.screen.botgame.BotGameViewModel.SideEffect.NavigateBack
import dev.mcd.chess.ui.screen.botgame.BotGameViewModel.SideEffect.NotifyGameCopied
import dev.mcd.chess.ui.screen.botgame.BotGameViewModel.State.Game
import dev.mcd.chess.ui.screen.botgame.BotGameViewModel.State.Loading
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun BotGameScreen(
    viewModel: BotGameViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(viewModel::onCopyPGN) {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = stringResource(R.string.copy_pgn),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            val state by viewModel.collectAsState()
            val context = LocalContext.current

            var showTermination by remember { mutableStateOf<AnnounceTermination?>(null) }
            var showResignation by remember { mutableStateOf<ConfirmResignation?>(null) }

            BackHandler {
                viewModel.onResign(andNavigateBack = true)
            }

            viewModel.collectSideEffect { effect ->
                when (effect) {
                    is AnnounceTermination -> showTermination = effect
                    is ConfirmResignation -> showResignation = effect
                    is NotifyGameCopied -> Toast.makeText(context, R.string.game_copied, Toast.LENGTH_SHORT).show()
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
                is Game -> {
                    GameView(
                        gameHolder = s.gameHolder,
                        onMove = viewModel::onPlayerMove,
                        onResign = viewModel::onResign,
                    )
                }
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
