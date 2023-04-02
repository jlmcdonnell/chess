package dev.mcd.chess.ui.extension

import androidx.compose.ui.geometry.Offset
import com.github.bhlangonijr.chesslib.Square
import dev.mcd.chess.ui.game.board.chessboard.BoardLayout

context(BoardLayout)
fun Square.topLeft(): Offset {
    return if (isWhite) {
        val x = file.ordinal * squareSize
        val y = (7 - rank.ordinal) * squareSize
        Offset(x, y)
    } else {
        val x = (7 - file.ordinal) * squareSize
        val y = rank.ordinal * squareSize
        Offset(x, y)
    }
}

context (BoardLayout)
fun Square.center(): Offset {
    return if (isWhite) {
        val x = file.ordinal * squareSize + squareSize / 2
        val y = (7 - rank.ordinal) * squareSize + squareSize / 2
        Offset(x, y)
    } else {
        val x = (7 - file.ordinal) * squareSize + squareSize / 2
        val y = rank.ordinal * squareSize + squareSize / 2
        Offset(x, y)
    }
}
