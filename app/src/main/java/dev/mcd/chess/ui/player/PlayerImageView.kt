package dev.mcd.chess.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.mcd.chess.domain.model.PlayerImage
import dev.mcd.chess.domain.model.PlayerImage.Local
import dev.mcd.chess.domain.model.PlayerImage.None
import dev.mcd.chess.domain.model.PlayerImage.Url

@Composable
fun PlayerImageView(image: PlayerImage) {
    val modifier = Modifier
        .clip(RoundedCornerShape(percent = 15))
        .background(Color.DarkGray)
        .size(40.dp)

    when (image) {
        is Local -> {
            Image(
                modifier = modifier,
                painter = painterResource(id = image.resId),
                contentScale = ContentScale.Crop,
                contentDescription = "Player",
            )
        }
        is Url,
        is None -> {
            Icon(
                modifier = modifier,
                imageVector = Icons.Default.Person,
                contentDescription = "Default",
            )
        }
    }
}
