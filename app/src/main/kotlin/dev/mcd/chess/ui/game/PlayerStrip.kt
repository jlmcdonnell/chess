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
import dev.mcd.chess.ui.player.PlayerImageView

@Composable
fun PlayerStrip(
    modifier: Modifier = Modifier,
    playerName: String,
    isBot: Boolean,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlayerImageView(bot = isBot)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = playerName,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}