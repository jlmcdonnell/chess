package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.extension.toDp
import dev.mcd.chess.ui.extension.topLeft

context(BoardLayout)
@Composable
fun MoveHighlight() {
    val lastMove by LocalGameSession.current.moveUpdates().collectAsState(null)

    lastMove?.let { (moveBackup, undo) ->

        val (from, to) = if (undo) {
            val previousMove = LocalGameSession.current.previousMove() ?: return@let

            previousMove.from to previousMove.to
        } else {
            moveBackup.move.from to moveBackup.move.to
        }
        val moveFromOffset = from.topLeft()
        val moveToOffset = to.topLeft()
        val (fromColor, toColor) = listOf(from, to).map {
            if (it.isLightSquare) BoardTheme.lastMoveHighlightOnLight else BoardTheme.lastMoveHighlightOnDark
        }

        Box(
            modifier = Modifier
                .testTag("highlight-from")
                .size(squareSizeDp)
                .offset(moveFromOffset.x.toDp(), moveFromOffset.y.toDp())
                .background(fromColor),
        )
        Box(
            modifier = Modifier
                .testTag("highlight-to")
                .size(squareSizeDp)
                .offset(moveToOffset.x.toDp(), moveToOffset.y.toDp())
                .background(toColor),
        )
    }
}
