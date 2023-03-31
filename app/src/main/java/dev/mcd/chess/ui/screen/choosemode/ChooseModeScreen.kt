package dev.mcd.chess.ui.screen.choosemode

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.mcd.chess.R
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.ui.LocalAppColors
import dev.mcd.chess.ui.screen.choosemode.ChooseModeViewModel.SideEffect.NavigateToExistingGame
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ChooseModeScreen(
    onPlayOnline: () -> Unit,
    onPlayBot: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateExistingGame: (GameId) -> Unit,
    viewModel: ChooseModeViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect {
        when (it) {
            is NavigateToExistingGame -> onNavigateExistingGame(it.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = { onNavigateSettings() }) {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Rounded.Settings),
                            contentDescription = stringResource(R.string.settings),
                        )
                    }
                },
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                PlayOnlineButton(
                    inLobby = state.inLobby,
                    onClick = onPlayOnline,
                )
                Spacer(modifier = Modifier.width(24.dp))
                PlayComputerButton(onClick = onPlayBot)
            }
        }
    }
}

context(ColumnScope)
@Composable
private fun PlayComputerButton(
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .weight(1f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.play_computer),
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(modifier = Modifier.height(36.dp))
            Icon(
                modifier = Modifier.size(32.dp),
                painter = rememberVectorPainter(image = Icons.Rounded.Computer),
                contentDescription = stringResource(R.string.play_computer),
            )
        }
    }
}

context(ColumnScope)
@Composable
private fun PlayOnlineButton(
    inLobby: Int?,
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.weight(1f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.play_online),
                style = MaterialTheme.typography.titleSmall,
            )
            if (inLobby != null) {
                val color = if (inLobby > 0) {
                    LocalAppColors.current.green
                } else {
                    Color.Unspecified
                }
                Text(
                    modifier = Modifier.height(24.dp),
                    text = stringResource(R.string.waiting, inLobby),
                    style = MaterialTheme.typography.bodySmall,
                    color = color,
                )
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                Spacer(modifier = Modifier.height(36.dp))
            }

            Icon(
                modifier = Modifier.size(32.dp),
                painter = rememberVectorPainter(image = Icons.Rounded.PersonSearch),
                contentDescription = stringResource(R.string.play_online),
            )
        }
    }
}
