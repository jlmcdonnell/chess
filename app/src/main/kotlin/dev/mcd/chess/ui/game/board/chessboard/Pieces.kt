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

context(BoardLayout)
@Composable
fun Pieces() {
    val gameManager = LocalGameSession.current
    val game by gameManager.sessionUpdates().collectAsState(null)
    val pieces = remember(game?.id) { game?.piecesAtVariationStart() ?: emptyList() }

    pieces.forEachIndexed { index, piece ->
        if (piece != Piece.NONE) {
            ChessPiece(
                initialState = calculatePieceState(
                    piece = piece,
                    initialSquare = Square.squareAt(index),
                    history = game?.history() ?: emptyList(),
                ),
            )
        }
    }
}

context(BoardLayout)
private fun calculatePieceState(
    piece: Piece,
    initialSquare: Square,
    history: List<MoveBackup>,
) = history.foldIndexed(
    initial = ChessPieceState(
        square = initialSquare,
        squareOffset = initialSquare.topLeft(),
        piece = piece,
        moveCount = -1,
    ),
) { index, state, move ->
    if (state.square.relevantToMove(move)) {
        val directionalMove = DirectionalMove(move, undo = false)
        UpdateChessPieceState(moveCount = index, directionalMove, state)
    } else state
}
