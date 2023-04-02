package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import dev.mcd.chess.ui.extension.center

fun Modifier.calculateBoardLayout(
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
                    squarePositions = Square.values().associateWith { square ->
                        square.center(perspective == Side.WHITE, squareSize)
                    },
                ).let(onLayout)
            }
        }
    }
}
