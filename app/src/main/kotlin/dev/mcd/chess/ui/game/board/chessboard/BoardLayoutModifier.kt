package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import com.github.bhlangonijr.chesslib.Side

fun Modifier.boardLayout(
    perspective: Side,
    onLayout: (BoardLayout) -> Unit,
): Modifier {
    return composed {
        val density = LocalDensity.current
        onGloballyPositioned {
            density.run {
                val squareSize = it.size.width.div(8).toFloat()
                val squareSizeDp = squareSize.toDp()
                BoardLayout(
                    squareSizeDp = squareSizeDp,
                    squareSize = squareSize,
                    perspective = perspective,
                ).let(onLayout)
            }
        }
    }
}
