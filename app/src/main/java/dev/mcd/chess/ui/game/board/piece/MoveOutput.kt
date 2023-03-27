package dev.mcd.chess.ui.game.board.piece

import androidx.compose.ui.geometry.Offset
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import dev.mcd.chess.common.game.DirectionalMove
import dev.mcd.chess.ui.extension.topLeft

data class MoveOutput(
    val newPiece: Piece,
    val newCaptured: Boolean,
    val newSquare: Square,
    val newSquareOffset: Offset,
)

object GetMoveOutputs {
    operator fun invoke(
        perspective: Side,
        size: Float,
        directionalMove: DirectionalMove,
        piece: Piece,
        captured: Boolean,
        square: Square,
    ): MoveOutput {
        val (moveBackup, undo) = directionalMove
        val move = moveBackup.move
        var newPiece = piece
        var newSquare = square
        var newCaptured = captured
        var newSquareOffset = square.topLeft(perspective, size)


        if (undo) {
            if (move.to == square) {
                if (move.promotion != Piece.NONE && piece == move.promotion) {
                    newPiece = moveBackup.movingPiece
                }
                if (moveBackup.capturedPiece == piece) {
                    newCaptured = false
                } else if (moveBackup.movingPiece == piece) {
                    newSquare = move.from
                    newSquareOffset = square.topLeft(perspective, size)
                }
            } else if (moveBackup.rookCastleMove?.to == square) {
                newSquare = moveBackup.rookCastleMove.from
                newSquareOffset = square.topLeft(perspective, size)
            } else if (moveBackup.enPassantTarget == square) {
                newCaptured = false
            }

        } else {
            if (!captured) {
                if (move.from == square) {
                    newSquare = move.to
                    newSquareOffset = move.to.topLeft(perspective, size)
                    if (move.promotion != Piece.NONE) {
                        newPiece = move.promotion
                    }
                } else if (piece == moveBackup.capturedPiece && square == moveBackup.capturedSquare) {
                    newCaptured = true
                } else if (moveBackup.rookCastleMove?.from == square) {
                    newSquare = moveBackup.rookCastleMove.to
                    newSquareOffset = square.topLeft(perspective, size)
                } else if (moveBackup.enPassantTarget == square) {
                    newSquare = moveBackup.enPassantTarget
                    newSquareOffset = square.topLeft(perspective, size)
                }
            }
        }

        return MoveOutput(
            newPiece = newPiece,
            newCaptured = newCaptured,
            newSquare = newSquare,
            newSquareOffset = newSquareOffset,
        )
    }
}
