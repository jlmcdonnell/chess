package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.ui.extension.drawableResource
import dev.mcd.chess.ui.extension.toDp

@Composable
fun rememberBoardLayout(
    width: Float? = null,
    perspective: Side,
): BoardLayout {
    val squareSize = if (width != null) {
        width / 8f
    } else {
        (LocalConfiguration.current.screenWidthDp * LocalDensity.current.density) / 8f
    }
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
