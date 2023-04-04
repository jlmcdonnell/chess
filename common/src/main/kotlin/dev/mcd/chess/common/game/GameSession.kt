package dev.mcd.chess.common.game

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.MoveBackup
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.common.player.HumanPlayer
import dev.mcd.chess.common.player.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import java.util.Stack

open class GameSession(
    val id: String = "",
    val self: Player = HumanPlayer(),
    val opponent: Player = HumanPlayer(),
    val selfSide: Side = Side.WHITE,
) {
    private lateinit var board: Board
    private lateinit var startingPieces: List<Piece>

    private val moves = MutableStateFlow<DirectionalMove?>(null)
    private val history = Stack<MoveBackup>()
    private val terminationReason = MutableStateFlow<TerminationReason?>(null)

    val moveCount: Int get() = board.backup.size

    suspend fun setBoard(board: Board) {
        this.board = board
        this.startingPieces = board.boardToArray().toList()

        board.backup.lastOrNull()?.let { moves.emit(DirectionalMove(it, undo = false)) }
    }

    suspend fun awaitTermination(): TerminationReason {
        return terminationReason.filterNotNull().first()
    }

    fun termination(): TerminationReason? {
        return terminationReason.value
    }

    open suspend fun move(move: String): MoveResult {
        return if (termination() != null) {
            MoveResult.GameTerminated
        } else {
            val moved = board.doMove(move)
            if (moved) {
                val moveBackup = board.backup.last
                moves.emit(DirectionalMove(moveBackup, undo = false))
                updateTermination()
                MoveResult.Moved
            } else {
                MoveResult.MoveIllegal
            }
        }
    }

    open suspend fun resign() {
        terminationReason.emit(TerminationReason(resignation = selfSide))
    }

    fun isSelfTurn() = selfSide == board.sideToMove

    fun undo(eraseHistory: Boolean = false) {
        if (board.backup.size > 0) {
            val lastMove = board.backup.last()
            board.undoMove()
            if (!eraseHistory) {
                history.push(lastMove)
            }
            moves.tryEmit(DirectionalMove(lastMove, undo = true))
        }
    }

    fun redo() {
        if (history.size > 0) {
            board.doMove(history.pop().move)
            moves.tryEmit(DirectionalMove(board.backup.last, undo = false))
        }
    }

    fun piecesAtVariationStart(): List<Piece> {
        return startingPieces
    }

    fun pieces(): List<Piece> {
        return board.boardToArray().toList()
    }

    fun isLive() = history.size == 0

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

    fun history(): List<MoveBackup> = board.backup

    private fun updateTermination() {
        val mated = board.sideToMove.takeIf { board.isMated }
        val draw = board.isDraw
        if (mated != null || draw) {
            terminationReason.tryEmit(
                TerminationReason(
                    sideMated = mated,
                    draw = draw,
                ),
            )
        }
    }
}
