package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.bhlangonijr.chesslib.MoveBackup
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Square
import dev.mcd.chess.common.game.DirectionalMove
import dev.mcd.chess.common.game.extension.relevantToMove
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.extension.topLeft
import dev.mcd.chess.ui.game.board.piece.ChessPiece
import dev.mcd.chess.ui.game.board.piece.ChessPieceState
import dev.mcd.chess.ui.game.board.piece.UpdateChessPieceState
import java.util.Stack

context(BoardLayout)
@Composable
fun Pieces() {
    val gameManager = LocalGameSession.current
    val game by gameManager.sessionUpdates().collectAsState(null)
    val pieces = remember(game?.id) { game?.piecesAtVariationStart() ?: emptyList() }

    pieces.forEachIndexed { index, piece ->
        if (piece != Piece.NONE) {
            val initialSquare = Square.squareAt(index)
            val history = game!!.history()
            val state = calculatePieceState(piece, initialSquare, history)

            ChessPiece(
                initialState = state,
            )
        }
    }
}

context(BoardLayout)
private fun calculatePieceState(
    piece: Piece,
    initialSquare: Square,
    history: List<MoveBackup>,
) = history.fold(
    initial = ChessPieceState(
        square = initialSquare,
        squareOffset = initialSquare.topLeft(),
        piece = piece,
        captured = false,
        moves = Stack(),
    ),
) { state, move ->
    if (state.square.relevantToMove(move)) {
        val directionalMove = DirectionalMove(move, undo = false)
        UpdateChessPieceState(directionalMove, state)
    } else state
}
