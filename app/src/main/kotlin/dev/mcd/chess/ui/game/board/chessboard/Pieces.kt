package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Square
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.extension.topLeft
import dev.mcd.chess.ui.game.board.piece.ChessPiece
import dev.mcd.chess.ui.game.board.piece.ChessPieceState

context(BoardLayout)
@Composable
fun Pieces() {
    val game by LocalGameSession.current.sessionUpdates().collectAsState(null)
    val pieces = remember(game?.id) { game?.pieces() ?: emptyList() }

    pieces.forEachIndexed { index, piece ->
        if (piece != Piece.NONE) {
            val initialSquare = Square.squareAt(index)
            ChessPiece(
                initialState = ChessPieceState(
                    square = initialSquare,
                    squareOffset = initialSquare.topLeft(),
                    piece = piece,
                    captured = false,
                ),
            )
        }
    }
}
