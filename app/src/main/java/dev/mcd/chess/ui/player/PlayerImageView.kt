package dev.mcd.chess.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.mcd.chess.common.player.PlayerImage
import dev.mcd.chess.common.player.PlayerImage.None
import dev.mcd.chess.common.player.PlayerImage.Url
import dev.mcd.chess.ui.LocalAppColors

@Composable
fun PlayerImageView(image: PlayerImage) {
    val modifier = Modifier
        .clip(CircleShape)
        .background(LocalAppColors.current.profileImageBackground)
        .size(40.dp)

    when (image) {
        is Url,
        is None -> {
            Icon(
                modifier = modifier.padding(8.dp),
                imageVector = Icons.Rounded.Person,
                contentDescription = "Default",
                tint = LocalAppColors.current.profileImageForeground
            )
        }
    }
}
