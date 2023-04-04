package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.ui.extension.drawableResource
import dev.mcd.chess.ui.extension.toDp

@Composable
fun rememberBoardLayout(
    boardWidth: Float,
    perspective: Side,
): BoardLayout {
    val squareSize = boardWidth / 8f
    val squareSizeDp = squareSize.toDp()
    val painters = remember { mutableMapOf<Piece, Painter>() }

    Piece.values().forEach { piece ->
        if (piece != Piece.NONE && !painters.containsKey(piece)) {
            painters[piece] = painterResource(piece.drawableResource())
        }
    }

    return remember(perspective) {
        BoardLayout(
            squareSizeDp = squareSizeDp,
            squareSize = squareSize,
            perspective = perspective,
            getPainter = { piece ->
                painters[piece]!!
            },
        )
    }
}
