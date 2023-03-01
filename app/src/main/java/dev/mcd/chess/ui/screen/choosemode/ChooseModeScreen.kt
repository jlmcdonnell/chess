package dev.mcd.chess.ui.screen.choosemode

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.mcd.chess.domain.game.SessionId
import dev.mcd.chess.ui.screen.choosemode.ChooseModeViewModel.*
import dev.mcd.chess.ui.screen.choosemode.ChooseModeViewModel.SideEffect.*
import dev.mcd.chess.ui.theme.LocalAppColors
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ChooseModeScreen(
    onPlayOnline: () -> Unit,
    onPlayBot: () -> Unit,
    onNavigateSettings: () -> Unit,
    onNavigateExistingGame: (SessionId) -> Unit,
    viewModel: ChooseModeViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect {
        when (it) {
            is NavigateToExistingGame -> onNavigateExistingGame(it.id)
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "Chess") },
            actions = {
                IconButton(onClick = { onNavigateSettings() }) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.Settings),
                        contentDescription = "Settings"
                    )
                }
            },
        )
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Card(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .clickable { onPlayOnline() }
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Play Online",
                            style = MaterialTheme.typography.subtitle1,
                        )
                        val inLobby = state.inLobby
                        if (inLobby != null) {
                            val color = if (inLobby > 0) {
                                LocalAppColors.current.green
                            } else {
                                Color.Unspecified
                            }
                            Text(
                                modifier = Modifier.height(24.dp),
                                text = "${state.inLobby} waiting",
                                style = MaterialTheme.typography.subtitle1,
                                color = color,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        } else {
                            Spacer(modifier = Modifier.height(36.dp))
                        }

                        Icon(
                            modifier = Modifier.size(32.dp),
                            painter = rememberVectorPainter(image = Icons.Rounded.PersonSearch),
                            contentDescription = "Play Online"
                        )
                    }
                }
                Spacer(modifier = Modifier.width(24.dp))
                Card(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .clickable { onPlayBot() }
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Play Computer",
                            style = MaterialTheme.typography.subtitle1,
                        )
                        Spacer(modifier = Modifier.height(36.dp))
                        Icon(
                            modifier = Modifier.size(32.dp),
                            painter = rememberVectorPainter(image = Icons.Rounded.Computer),
                            contentDescription = "Play Computer"
                        )
                    }
                }
            }
        }
    }
}
