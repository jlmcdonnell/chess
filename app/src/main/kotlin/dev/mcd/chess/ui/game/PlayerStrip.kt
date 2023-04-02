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
import dev.mcd.chess.common.player.Player
import dev.mcd.chess.common.player.PlayerImage.Default
import dev.mcd.chess.ui.player.PlayerImageView

@Composable
fun PlayerStrip(
    modifier: Modifier = Modifier,
    player: Player? = null,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerImageView(player?.image ?: Default)
        Spacer(modifier = Modifier.width(12.dp))
        player?.let {
            Column {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}
