package dev.mcd.chess.ui.game

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiFlags
import androidx.compose.material.icons.rounded.FlipCameraAndroid
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mcd.chess.R

@Composable
fun GameOptions(
    modifier: Modifier = Modifier,
    onResignClicked: () -> Unit,
    terminated: Boolean,
    onUndoClicked: () -> Unit,
    onRedoClicked: () -> Unit,
    onFlipBoard: () -> Unit,
) {
    Row(modifier) {
        if (!terminated) {
            IconButton(
                onClick = { onResignClicked() },
            ) {
                Icon(
                    imageVector = Icons.Rounded.EmojiFlags,
                    contentDescription = stringResource(R.string.resign),
                )
            }
        }
        FlipBoard(onFlipBoard)
        UndoMove(onUndoClicked)
        RedoMove(onRedoClicked)
    }
}

@Composable
fun UndoMove(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(
            imageVector = Icons.Rounded.Undo,
            contentDescription = stringResource(R.string.undo_move),
        )
    }
}

@Composable
fun RedoMove(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(
            imageVector = Icons.Rounded.Redo,
            contentDescription = stringResource(R.string.redo_move),
        )
    }
}

@Composable
fun FlipBoard(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(
            imageVector = Icons.Rounded.FlipCameraAndroid,
            contentDescription = stringResource(R.string.flip_board),
        )
    }
}
