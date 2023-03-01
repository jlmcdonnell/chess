package dev.mcd.chess.ui.game

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.DoorBack
import androidx.compose.material.icons.rounded.DoorFront
import androidx.compose.material.icons.rounded.Emergency
import androidx.compose.material.icons.rounded.EmojiFlags
import androidx.compose.material.icons.rounded.TimeToLeave
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun GameOptions(
    modifier: Modifier = Modifier,
    onResignClicked: () -> Unit,
) {
    Row(modifier) {
        IconButton(
            onClick = { onResignClicked() }
        ) {
            Icon(
                imageVector = Icons.Rounded.EmojiFlags,
                contentDescription = "Resign",
            )
        }
    }
}
