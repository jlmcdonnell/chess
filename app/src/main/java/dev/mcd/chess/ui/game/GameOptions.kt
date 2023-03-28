package dev.mcd.chess.ui.game

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiFlags
import androidx.compose.material.icons.rounded.LocalPrintshop
import androidx.compose.material.icons.rounded.Print
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.mcd.chess.BuildConfig
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.ui.LocalGameSession

@Composable
fun GameOptions(
    modifier: Modifier = Modifier,
    onResignClicked: () -> Unit,
    terminated: Boolean,
    onUndoClicked: () -> Unit,
    onRedoClicked: () -> Unit,
) {
    val game by LocalGameSession.current.sessionUpdates().collectAsState(GameSession())

    Row(modifier) {
        if (!terminated) {
            IconButton(
                onClick = { onResignClicked() }
            ) {
                Icon(
                    imageVector = Icons.Rounded.EmojiFlags,
                    contentDescription = "Resign",
                )
            }
        }
        UndoMove(onUndoClicked)
        RedoMove(onRedoClicked)
        if (BuildConfig.DEBUG) {
            PrintPGN(game)
        }
    }
}

@Composable
fun PrintPGN(game: GameSession) {
    IconButton(onClick = {
        TODO()
    }) {
        Icon(
            imageVector = Icons.Rounded.Print,
            contentDescription = "Print PGN",
        )
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
