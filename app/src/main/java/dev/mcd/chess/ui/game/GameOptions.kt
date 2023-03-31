package dev.mcd.chess.ui.game

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiFlags
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun GameOptions(
    modifier: Modifier = Modifier,
    onResignClicked: () -> Unit,
    terminated: Boolean,
    onUndoClicked: () -> Unit,
    onRedoClicked: () -> Unit,
) {
    Row(modifier) {
        if (!terminated) {
            IconButton(
                onClick = { onResignClicked() },
            ) {
                Icon(
                    imageVector = Icons.Rounded.EmojiFlags,
                    contentDescription = "Resign",
                )
            }
        }
        UndoMove(onUndoClicked)
        RedoMove(onRedoClicked)
    }
}

@Composable
fun UndoMove(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(
            imageVector = Icons.Rounded.Undo,
            contentDescription = "Undo move",
        )
    }
}

@Composable
fun RedoMove(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(
            imageVector = Icons.Rounded.Redo,
            contentDescription = "Redo move",
        )
    }
}
