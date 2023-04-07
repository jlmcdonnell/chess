package dev.mcd.chess.ui.game.board.interaction

import androidx.compose.ui.geometry.Offset
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.ui.extension.center
import dev.mcd.chess.ui.game.board.chessboard.BoardLayout
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlin.math.pow
import kotlin.math.sqrt

class BoardInteraction(
    private val session: GameSession,
) {
    private val perspective = MutableStateFlow(session.selfSide)
    private val moves = MutableSharedFlow<Move>(replay = 1, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val target = MutableStateFlow(Square.NONE)
    private val highlightMoveChanges = MutableStateFlow(Square.NONE)
    private var squarePositions: Map<Square, Offset> = emptyMap()
    private var enableInteraction = true
    private val selectPromotion = MutableStateFlow(emptyList<Move>())
    private var promotionCompletable: CompletableDeferred<Move?>? = null

    fun updateSquarePositions(squareSizePx: Float) {
        this.squarePositions = Square.values().associateWith { square ->
            square.center(perspective.value == Side.WHITE, squareSizePx)
        }
    }

    fun promote(move: Move) {
        if (enableInteraction) {
            promotionCompletable?.complete(move)
        }
    }

    suspend fun selectPromotion(promotionMoves: List<Move>): Boolean {
        if (enableInteraction) {
            promotionCompletable = CompletableDeferred()

            selectPromotion.value = promotionMoves
            val move = promotionCompletable!!.await()

            releaseTarget()
            clearHighlightMoves()
            selectPromotion.value = emptyList()

            if (move != null) {
                moves.tryEmit(move)
                return true
            }
        }
        return false
    }

    fun cancelPromotion() {
        promotionCompletable?.complete(null)
    }

    fun displayPromotions(): Flow<List<Move>> {
        return selectPromotion
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
            if (target.value == Square.NONE) return DropPieceResult.None

            var result: DropPieceResult = DropPieceResult.None

            if (session.selfSide == piece.pieceSide) {
                val move = Move(square, target.value)
                val promotions = session.promotions(move)

                if (move in session.legalMoves()) {
                    moves.tryEmit(move)
                    result = DropPieceResult.Moved(square, target.value)
                    clearHighlightMoves()
                    releaseTarget()
                } else if (promotions.isNotEmpty()) {
                    result = DropPieceResult.SelectPromotion(promotions)
                    clearHighlightMoves()
                } else {
                    releaseTarget()
                    clearHighlightMoves()
                }
            }
            return result
        } else {
            clearHighlightMoves()
            releaseTarget()
            return DropPieceResult.None
        }
    }

    fun moves(): Flow<Move> {
        return moves.filter { it.to != Square.NONE && it.from != Square.NONE }
    }

    fun targets(): Flow<Square> {
        return target
    }

    fun perspectiveChanges(): Flow<Side> = perspective

    fun perspective(): Side = perspective.value

    fun togglePerspective() {
        perspective.value = if (perspective.value == Side.WHITE) Side.BLACK else Side.WHITE
    }

    fun highlightMovesFrom(): Flow<Square> = highlightMoveChanges

    fun setInteractionEnabled(enableInteraction: Boolean) {
        this.enableInteraction = enableInteraction
        if (!enableInteraction) {
            releaseTarget()
            highlightMoveChanges.tryEmit(Square.NONE)
            selectPromotion.tryEmit(emptyList())
        }
    }

    private fun releaseTarget() {
        target.value = Square.NONE
    }
}
