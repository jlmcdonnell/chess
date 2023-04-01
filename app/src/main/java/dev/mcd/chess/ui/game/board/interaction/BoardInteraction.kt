package dev.mcd.chess.ui.game.board.interaction

import androidx.compose.ui.geometry.Offset
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.ui.game.board.chessboard.BoardLayout
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import timber.log.Timber
import kotlin.math.pow
import kotlin.math.sqrt

class BoardInteraction(
    private val session: GameSession = GameSession(),
) {

    private val perspective = MutableStateFlow(session.selfSide)
    private val moves = MutableSharedFlow<Move>(replay = 1, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val target = MutableStateFlow(Square.NONE)
    private val highlightMoveChanges = MutableStateFlow(Square.NONE)
    private var squarePositions: Map<Square, Offset> = emptyMap()
    private val selectPromotion = MutableStateFlow(emptyList<Move>())
    private var enableInteraction = true

    fun updateSquarePositions(squarePositions: Map<Square, Offset>) {
        this.squarePositions = squarePositions
    }

    fun promote(move: Move) {
        if (enableInteraction) {
            selectPromotion.value = emptyList()
            moves.tryEmit(move)
            releaseTarget()
        }
    }

    fun selectPromotion(moves: List<Move>) {
        if (enableInteraction) {
            selectPromotion.value = moves
        }
    }

    fun highlightMoves(from: Square) {
        if (enableInteraction) {
            highlightMoveChanges.value = from
        }
    }

    fun clearHighlightMoves() {
        if (enableInteraction) {
            highlightMoveChanges.value = Square.NONE
        }
    }

    context(BoardLayout)
    fun updateDragPosition(position: Offset) {
        if (enableInteraction) {
            var closest: Pair<Square, Float>? = null
            for ((sq, offset) in squarePositions) {
                val (x1, y1) = offset
                val (x2, y2) = position
                val distance = sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))

                if ((closest == null || distance < closest.second) && distance <= squareSize) {
                    closest = sq to distance
                }
            }
            val newTarget = closest?.first ?: Square.NONE
            target.value = newTarget
        }
    }

    fun dropPiece(piece: Piece, square: Square): DropPieceResult {
        if (enableInteraction) {
            Timber.d("dropPiece $piece square=$square target=$target")
            if (target.value == Square.NONE) return DropPieceResult.None

            var result: DropPieceResult = DropPieceResult.None

            if (session.selfSide == piece.pieceSide) {
                val move = Move(square, target.value)
                val promotions = session.promotions(move)

                if (move in session.legalMoves()) {
                    if (moves.tryEmit(move)) {
                        Timber.d("dropPiece emit move: $this")
                    }
                    result = DropPieceResult.Moved(square, target.value)
                    clearHighlightMoves()
                } else if (promotions.isNotEmpty()) {
                    selectPromotion(promotions)
                    result = DropPieceResult.Promoting
                }
            }

            if (result != DropPieceResult.Promoting) {
                releaseTarget()
            }
            return result
        } else {
            return DropPieceResult.None
        }
    }

    fun moves(): Flow<Move> {
        return moves.filter { it.to != Square.NONE && it.from != Square.NONE }
    }

    fun targets(): Flow<Square> {
        return target
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
        target.value = Square.NONE
    }

    fun enableInteraction(enableInteraction: Boolean) {
        this.enableInteraction = enableInteraction
        if (!enableInteraction) {
            releaseTarget()
            highlightMoveChanges.tryEmit(Square.NONE)
            selectPromotion.tryEmit(emptyList())
        }
    }
}
