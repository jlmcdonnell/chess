package dev.mcd.chess.ui.game.board.interaction

import androidx.compose.ui.geometry.Offset
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.common.game.local.ClientGameSession
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlin.math.pow
import kotlin.math.sqrt


class BoardInteraction {

    var session: ClientGameSession? = null

    private val perspective = MutableStateFlow(Side.WHITE)
    private val moves = MutableSharedFlow<Move>(replay=1, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val targetChanges = MutableStateFlow(Square.NONE)
    private val highlightMoveChanges = MutableStateFlow(Square.NONE)
    private var squarePositions: Map<Square, Offset> = emptyMap()
    private var squareSize = 0f
    private val selectPromotion = MutableStateFlow(emptyList<Move>())

    var target: Square = Square.NONE
        private set(value) {
            field = value
            targetChanges.value = field
        }

    var highlightMovesFrom: Square = Square.NONE
        private set(value) {
            field = value
            highlightMoveChanges.value = field
        }

    fun updateSquareData(squarePositions: Map<Square, Offset>, squareSize: Float) {
        this.squarePositions = squarePositions
        this.squareSize = squareSize
    }


    fun promote(move: Move) {
        selectPromotion.value = emptyList()
        moves.tryEmit(move)
        releaseTarget()
    }

    fun selectPromotion(moves: List<Move>) {
        selectPromotion.value = moves
    }

    fun highlightMoves(from: Square) {
        highlightMovesFrom = from
    }

    fun disableHighlightMoves() {
        highlightMovesFrom = Square.NONE
    }

    fun updateDragPosition(position: Offset) {
        var closest: Pair<Square, Float>? = null
        for ((sq, offset) in squarePositions) {
            val (x1, y1) = offset
            val (x2, y2) = position
            val distance = sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))

            if ((closest == null || distance < closest.second) && distance <= squareSize) {
                closest = sq to distance
            }
        }
        target = closest?.first ?: Square.NONE
    }

    fun dropPiece(piece: Piece, square: Square): DropPieceResult {
        if (target == Square.NONE) return DropPieceResult.None
        val session = session ?: return DropPieceResult.None

        var result: DropPieceResult = DropPieceResult.None

        if (session.selfSide == piece.pieceSide) {
            val target = target
            val move = Move(square, target)
            val promotions = session.promotions(move)

            if (move in session.legalMoves()) {
                moves.tryEmit(move)
                result = DropPieceResult.Moved(target)
            } else if (promotions.isNotEmpty()) {
                selectPromotion(promotions)
                result = DropPieceResult.Promoting
            }
        }

        if (result != DropPieceResult.Promoting) {
            releaseTarget()
        }
        return result
    }

    fun moves(): Flow<Move> {
        return moves.filter { it.to != Square.NONE && it.from != Square.NONE }
    }

    fun targets(): Flow<Square> {
        return targetChanges
    }

    fun selectPromotion(): Flow<List<Move>> {
        return selectPromotion
    }

    fun perspective(): Flow<Side> = perspective

    fun setPerspective(side: Side) {
        perspective.value = side
    }

    fun highlightMovesFrom(): Flow<Square> = highlightMoveChanges

    private fun releaseTarget() {
        target = Square.NONE
    }
}
