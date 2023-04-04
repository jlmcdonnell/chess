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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mcd.chess.R
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.LocalGameSession

@Composable
fun GameOptions(
    modifier: Modifier = Modifier,
    onResignClicked: () -> Unit,
    allowResign: Boolean = true,
) {
    val boardInteraction = LocalBoardInteraction.current
    val game by LocalGameSession.current.sessionUpdates().collectAsState(GameSession())
    var isLive by remember { mutableStateOf(game.isLive()) }
    val interactionEnabled = remember(game.termination(), isLive) { game.termination() == null && isLive }

    LaunchedEffect(interactionEnabled) {
        boardInteraction.setInteractionEnabled(interactionEnabled)
    }

    Row(modifier) {
        if (game.termination() == null && allowResign) {
            IconButton(
                onClick = { onResignClicked() },
            ) {
                Icon(
                    imageVector = Icons.Rounded.EmojiFlags,
                    contentDescription = stringResource(R.string.resign),
                )
            }
        }
        FlipBoard {
            boardInteraction.togglePerspective()
        }
        UndoMove {
            game.undo()
            isLive = game.isLive()
        }
        RedoMove {
            game.redo()
            isLive = game.isLive()
        }
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
