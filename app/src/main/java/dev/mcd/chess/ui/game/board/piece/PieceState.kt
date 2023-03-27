package dev.mcd.chess.ui.game.board.piece

import androidx.compose.ui.geometry.Offset
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import dev.mcd.chess.ui.extension.topLeft
import dev.mcd.chess.ui.game.board.GameSessionManager
import kotlinx.coroutines.flow.collectLatest

class PieceState(
    val sessionManager: GameSessionManager,
) {

    lateinit var square: Square
    lateinit var piece: Piece
    lateinit var perspective: Side
    var squareOffset: Offset = Offset.Zero
    var size: Float = 0f
    var captured = false

    fun init(square: Square) {
        this.square = square

    }

    suspend fun collect() {
        sessionManager.moveUpdates().collectLatest { (moveBackup, undo) ->
            val move = moveBackup.move
            if (move.from == square) {
                square = move.to
                squareOffset = move.to.topLeft(perspective, size)
                if (move.promotion != Piece.NONE) {
                    piece = move.promotion
                }
            } else if (piece == moveBackup.capturedPiece && square == moveBackup.capturedSquare) {
                captured = true
            } else if (moveBackup.rookCastleMove?.from == square) {
                square = moveBackup.rookCastleMove.to
                squareOffset = square.topLeft(perspective, size)
            } else if (moveBackup.enPassantTarget == square) {
                square = moveBackup.enPassantTarget
                squareOffset = square.topLeft(perspective, size)
            }
        }
    }
}
