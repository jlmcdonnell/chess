package dev.mcd.chess.common.game

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.common.player.HumanPlayer
import dev.mcd.chess.common.player.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import java.util.Stack

open class GameSession(
    val id: String = "",
    val self: Player = HumanPlayer(),
    val opponent: Player = HumanPlayer(),
    val selfSide: Side = Side.WHITE,
) {
    private lateinit var board: Board

    private val moves = MutableStateFlow<DirectionalMove?>(null)

    val moveCount: Int get() = board.moveCounter

    private val history = Stack<Move>()

    suspend fun setBoard(board: Board) {
        this.board = board
        board.backup.lastOrNull()?.let { moves.emit(DirectionalMove(it, undo = false)) }
    }

    open suspend fun move(move: String): MoveResult {
        val moved = board.doMove(move)
        return if (moved) {
            val moveBackup = board.backup.last
            moves.emit(DirectionalMove(moveBackup, undo = false))
            MoveResult.Moved
        } else {
            MoveResult.MoveIllegal
        }
    }

    fun undo() {
        if (board.backup.size > 0) {
            val lastMove = board.backup.last()
            history.push(board.undoMove())
            moves.tryEmit(DirectionalMove(lastMove, undo = true))
        }
    }

    fun redo() {
        if (history.size > 0) {
            board.doMove(history.pop())
            moves.tryEmit(DirectionalMove(board.backup.last, undo = false))
        }
    }

    fun pieces(): List<Piece> {
        return board.boardToArray().toList()
    }

    fun isLive() = history.size == 0

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

    fun previousMove(): Move? = board.backup.lastOrNull()?.move

    fun legalMoves() = board.legalMoves().toList()

    fun promotions(move: Move): List<Move> = legalMoves()
        .filter { it.from == move.from && it.to == move.to }
        .filter { it.promotion != Piece.NONE }

    fun captures() = board.backup
        .mapNotNull { it.capturedPiece }
        .filter { it != Piece.NONE }

    fun fen() = board.fen!!
}
