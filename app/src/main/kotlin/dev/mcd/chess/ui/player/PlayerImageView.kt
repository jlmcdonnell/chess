package dev.mcd.chess.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.mcd.chess.R
import dev.mcd.chess.common.player.PlayerImage

@Composable
fun PlayerImageView(image: PlayerImage) {
    val vector = if (image is PlayerImage.Bot) {
        Icons.Rounded.Memory
    } else {
        Icons.Rounded.Person
    }
    Icon(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .size(24.dp)
            .padding(3.dp),
        imageVector = vector,
        contentDescription = stringResource(R.string.default_player_image_desc),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
