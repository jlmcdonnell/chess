package dev.mcd.chess.feature.game.domain

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.MoveBackup
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.common.game.TerminationReason
import dev.mcd.chess.common.player.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import timber.log.Timber

open class ClientGameSession(
    val id: String,
    val self: Player,
    val selfSide: Side,
    val opponent: Player,
) {
    private lateinit var board: Board

    private val pieceUpdates = MutableStateFlow<List<Piece>>(emptyList())
    private val moves = MutableStateFlow<MoveBackup?>(null)

    suspend fun setBoard(board: Board) {
        this.board = board
        board.backup.lastOrNull()?.let { moves.emit(it) }
        pieceUpdates.emit(board.boardToArray().toList())
    }

    suspend fun move(move: String, requireMoveCount: Int? = null): Boolean {
        val lastMove = board.backup.lastOrNull()?.move
        val moveCount = board.moveCounter
        if ((lastMove.toString() == move) && (moveCount == requireMoveCount)) {
            Timber.d("Ignoring the same move count=$moveCount move=${move}")
            return true
        }
        return board.doMove(move).also { moved ->
            if (moved) {
                val moveBackup = board.backup.last
                moves.emit(moveBackup)
                pieceUpdates.emit(board.boardToArray().toList())
            }
        }
    }

    fun termination(): TerminationReason? {
        val mated = board.sideToMove.takeIf { board.isMated }
        val draw = board.isDraw
        return if (mated != null || draw) {
            TerminationReason(
                sideMated = mated,
                draw = draw,
            )
        } else {
            null
        }
    }

    fun moves(): Flow<MoveBackup> = moves.filterNotNull()

    fun lastMove(): MoveBackup? = moves.value

    fun legalMoves() = board.legalMoves().toList()

    fun promotions(move: Move): List<Move> = legalMoves()
        .filter { it.from == move.from && it.to == move.to }
        .filter { it.promotion != Piece.NONE }

    fun captures() = board.backup
        .mapNotNull { it.capturedPiece }
        .filter { it != Piece.NONE }

    fun fen() = board.fen!!

    fun pieceUpdates() = pieceUpdates
}
