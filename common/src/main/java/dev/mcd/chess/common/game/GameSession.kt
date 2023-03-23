package dev.mcd.chess.common.game

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.common.player.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

open class GameSession(
    val id: String,
    val self: Player,
    val selfSide: Side,
    val opponent: Player,
) {
    private lateinit var board: Board

    private val pieceUpdates = MutableStateFlow<List<Piece>>(emptyList())
    private val moves = MutableStateFlow<DirectionalMove?>(null)

    val moveCount: Int get() = board.moveCounter

    suspend fun setBoard(board: Board) {
        this.board = board
        board.backup.lastOrNull()?.let { moves.emit(DirectionalMove(it, forward = true)) }
        pieceUpdates.emit(board.boardToArray().toList())
    }

    open suspend fun move(move: String): MoveResult {
        val moved = board.doMove(move)
        return if (moved) {
            val moveBackup = board.backup.last
            moves.emit(DirectionalMove(moveBackup, forward = true))
            pieceUpdates.emit(board.boardToArray().toList())
            MoveResult.Moved
        } else {
            MoveResult.MoveIllegal
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

    fun moves(): Flow<DirectionalMove> = moves.filterNotNull()

    fun lastMove(): DirectionalMove? = moves.value

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
