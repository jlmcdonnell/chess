package dev.mcd.chess.ui.game.board

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.geometry.Offset
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlin.math.pow
import kotlin.math.sqrt

val LocalBoardInteraction = compositionLocalOf { BoardInteraction() }

class BoardInteraction {

    private val perspective = MutableStateFlow(Side.WHITE)
    private val moves = MutableStateFlow(Move(Square.NONE, Square.NONE))
    private val targetChanges = MutableStateFlow(Square.NONE)
    private val highlightMoveChanges = MutableStateFlow(Square.NONE)
    private var squarePositions: Map<Square, Offset> = emptyMap()
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

    fun updateSquarePositions(squarePositions: Map<Square, Offset>) {
        this.squarePositions = squarePositions
    }

    fun placePieceFrom(from: Square): Boolean {
        return if (target != Square.NONE) {
            val move = Move(from, target)
            moves.value = move
            target = Square.NONE
            true
        } else false
    }

    fun promote(move: Move) {
        selectPromotion.value = emptyList()
        moves.value = move
        releaseTarget()
    }

    fun releaseTarget() {
        target = Square.NONE
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

            if (closest == null || distance < closest.second) {
                closest = sq to distance
            }
        }
        target = closest?.first ?: Square.NONE
    }

    fun moves(): Flow<Move> {
        return moves.filter { it.to != Square.NONE && it.from != Square.NONE }
            .distinctUntilChanged()
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
}
