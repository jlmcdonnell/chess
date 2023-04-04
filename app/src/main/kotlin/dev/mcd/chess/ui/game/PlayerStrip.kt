package dev.mcd.chess.ui.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mcd.chess.common.player.Bot
import dev.mcd.chess.common.player.HumanPlayer
import dev.mcd.chess.common.player.Player
import dev.mcd.chess.common.player.PuzzleOpponent
import dev.mcd.chess.ui.player.BotImageView
import dev.mcd.chess.ui.player.PlayerImageView
import dev.mcd.chess.ui.player.PuzzleImageView

@Composable
fun PlayerStrip(
    modifier: Modifier,
    player: Player,
) {
    when (player) {
        is HumanPlayer -> HumanPlayerStrip(
            modifier = modifier,
            playerName = player.name,
        )

        is Bot -> BotPlayerStrip(
            modifier = modifier,
            playerName = player.name,
        )

        is PuzzleOpponent -> PuzzlePlayerStrip(
            modifier = modifier,
            playerName = player.name,
        )
    }
}

@Composable
fun BotPlayerStrip(
    modifier: Modifier = Modifier,
    playerName: String,
) {
    IconNamePlayerStrip(
        modifier = modifier,
        name = playerName,
        icon = { BotImageView() },
    )
}

@Composable
fun PuzzlePlayerStrip(
    modifier: Modifier = Modifier,
    playerName: String,
) {
    IconNamePlayerStrip(
        modifier = modifier,
        name = playerName,
        icon = { PuzzleImageView() },
    )
}

@Composable
fun HumanPlayerStrip(
    modifier: Modifier = Modifier,
    playerName: String,
) {
    IconNamePlayerStrip(
        modifier = modifier,
        name = playerName,
        icon = { PlayerImageView() },
    )
}

@Composable
fun IconNamePlayerStrip(
    modifier: Modifier = Modifier,
    name: String,
    icon: @Composable () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon()
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}
