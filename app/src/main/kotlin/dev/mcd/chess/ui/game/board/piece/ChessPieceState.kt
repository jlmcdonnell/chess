package dev.mcd.chess.ui.game.board.piece

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Square
import dev.mcd.chess.ui.game.board.chessboard.BoardLayout
import java.util.Stack

@Stable
data class ChessPieceState(
    var square: Square,
    var squareOffset: Offset,
    var piece: Piece,
    var captured: Boolean = false,
    var moveCount: Int = -1,
    val moves: Stack<String> = Stack(),
) {
    context(BoardLayout)
    val position: Offset
        get() = squareOffset + Offset(squareSize / 2f, squareSize / 2f)
}
