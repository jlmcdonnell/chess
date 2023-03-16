package dev.mcd.chess.ui.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mcd.chess.common.player.Player
import dev.mcd.chess.common.player.PlayerImage.None
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
        PlayerImageView(player?.image ?: None)
        Spacer(modifier = Modifier.width(12.dp))
        player?.let {
            Column {
                Text(
                    text = player.name,
                    fontSize = 18.sp,
                )
            }
        }
    }
}


