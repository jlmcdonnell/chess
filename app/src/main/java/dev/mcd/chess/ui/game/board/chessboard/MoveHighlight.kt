package dev.mcd.chess.ui.game.board.chessboard


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.ui.LocalBoardTheme
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.extension.toDp
import dev.mcd.chess.ui.extension.toPx
import dev.mcd.chess.ui.extension.topLeft

@Composable
fun MoveHighlight(
    perspective: Side,
    squareSize: Dp,
) {
    val boardTheme = LocalBoardTheme.current
    val lastMove by LocalGameSession.current.moveUpdates().collectAsState(null)
    val squareSizePx = squareSize.toPx()

    lastMove?.let { (moveBackup, undo) ->

        val (from, to) = if (undo) {
            val previousMove = LocalGameSession.current.previousMove() ?: return@let

            previousMove.from to previousMove.to
        } else {
            moveBackup.move.from to moveBackup.move.to
        }
        val moveFromOffset = from.topLeft(perspective, squareSizePx)
        val moveToOffset = to.topLeft(perspective, squareSizePx)
        Box(
            modifier = Modifier
                .testTag("highlight-from")
                .size(squareSize)
                .offset(moveFromOffset.x.toDp(), moveFromOffset.y.toDp())
                .background(boardTheme.lastMoveHighlight)
        )
        Box(
            modifier = Modifier
                .testTag("highlight-to")
                .size(squareSize)
                .offset(moveToOffset.x.toDp(), moveToOffset.y.toDp())
                .background(boardTheme.lastMoveHighlight)
        )
    }
}
