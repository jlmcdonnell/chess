package dev.mcd.chess.ui.screen.onlinegame

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.mcd.chess.R
import dev.mcd.chess.ui.compose.StableHolder
import dev.mcd.chess.ui.game.GameTermination
import dev.mcd.chess.ui.game.GameView
import dev.mcd.chess.ui.game.ResignationDialog
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.SideEffect.AnnounceTermination
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.SideEffect.ConfirmResignation
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.SideEffect.NavigateBack
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.SideEffect.NotifyGameCopied
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.State.FatalError
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.State.FindingGame
import dev.mcd.chess.ui.screen.onlinegame.OnlineGameViewModel.State.InGame
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber

@Composable
fun OnlineGameScreen(
    viewModel: OnlineGameViewModel = hiltViewModel(),
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
            var showTermination by remember { mutableStateOf<AnnounceTermination?>(null) }
            var showResignation by remember { mutableStateOf<ConfirmResignation?>(null) }
            val context = LocalContext.current

            BackHandler {
                Timber.d("Back pressed")
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
                is InGame -> GameView(
                    gameHolder = StableHolder(s.session),
                    onMove = viewModel::onPlayerMove,
                    onResign = viewModel::onResign,
                )

                is FindingGame -> FindingGameView(s.username, navigateBack)
                is FatalError -> Text(
                    text = """
                        Error:
                        ${s.message}
                    """,
                    color = Color.Red,
                )
            }

            showTermination?.let { (sideMated, draw, resignation) ->
                Spacer(modifier = Modifier.height(24.dp))
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

@Composable
private fun FindingGameView(
    username: String? = null,
    onCancel: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (username != null) {
                Text(
                    text = stringResource(R.string.playing_as, username),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.finding_game),
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                TextButton(onClick = { onCancel() }) {
                    Text(text = stringResource(R.string.cancel))
                }
            } else {
                Text(text = stringResource(R.string.authenticating))
            }
        }
    }
}
