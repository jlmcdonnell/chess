package dev.mcd.chess.ui.game.board.piece

import androidx.compose.ui.geometry.Offset
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import java.util.Stack

data class ChessPieceState(
    var square: Square,
    var squareOffset: Offset,
    var piece: Piece,
    var captured: Boolean = false,
    val moves: Stack<Move> = Stack()
)
