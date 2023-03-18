package dev.mcd.chess.ui.extension

import androidx.compose.ui.geometry.Offset
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square

fun Square.topLeft(perspective: Side, squareSize: Float): Offset {
    return if (perspective == Side.WHITE) {
        val x = file.ordinal * squareSize
        val y = (7 - rank.ordinal) * squareSize
        Offset(x, y)
    } else {
        val x = (7 - file.ordinal) * squareSize
        val y = rank.ordinal * squareSize
        Offset(x, y)
    }
}

fun Square.center(perspective: Side, squareSize: Float): Offset {
    return if (perspective == Side.WHITE) {
        val x = file.ordinal * squareSize + squareSize / 2
        val y = (7 - rank.ordinal) * squareSize + squareSize / 2
        Offset(x, y)
    } else {
        val x = (7 - file.ordinal) * squareSize + squareSize / 2
        val y = rank.ordinal * squareSize + squareSize / 2
        Offset(x, y)
    }
}
