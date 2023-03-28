package dev.mcd.chess.ui.game.board.piece

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.zIndex
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import dev.mcd.chess.common.game.extension.relevantToSquare
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.extension.drawableResource
import dev.mcd.chess.ui.extension.orZero
import dev.mcd.chess.ui.extension.toDp
import dev.mcd.chess.ui.extension.topLeft
import dev.mcd.chess.ui.game.board.interaction.DropPieceResult
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapNotNull

private const val PIECE_DRAG_SCALE = 1.7f


data class PieceSquare(val square: Square, val piece: Piece)

val PieceSquareKey = SemanticsPropertyKey<PieceSquare>("PieceSquareKey")
var SemanticsPropertyReceiver.pieceSquare by PieceSquareKey

@Composable
fun ChessPiece(
    size: Float,
    perspective: Side,
    initialState: ChessPieceState,
) {
    val gameManager = LocalGameSession.current
    val boardInteraction = LocalBoardInteraction.current

    var currentSize by remember { mutableStateOf(size) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var dragging by remember { mutableStateOf(false) }
    var state by remember { mutableStateOf(initialState) }

    LaunchedEffect(state.square) {
        gameManager
            .moveUpdates()
            .mapNotNull { gameManager.lastMove() }
            .relevantToSquare(state.square)
            .collectLatest { directionalMove ->
                state = UpdateChessPieceState(perspective, size, directionalMove, state)
            }
    }

    val animatedSize by animateDpAsState(currentSize.toDp(), label = "Piece Size")

    val position = remember(state, pan, dragging) {
        if (dragging) {
            Offset(
                x = (pan.x - animatedSize.value / 2f) + state.squareOffset.x,
                y = (pan.y - animatedSize.value * 2f) + state.squareOffset.y,
            )
        } else {
            Offset(
                x = pan.x + state.squareOffset.x,
                y = pan.y + state.squareOffset.y,
            )
        }
    }

    val dropping = animatedSize != currentSize.toDp()
    val animatedPan by animateOffsetAsState(
        targetValue = position,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "Piece Move"
    )

    if (state.captured) {
        return
    }


    Image(
        modifier = Modifier
            .semantics {
                pieceSquare = PieceSquare(state.square, state.piece)
            }
            .alpha(if (state.captured) 0.2f else 1f)
            .size(animatedSize)
            .zIndex(if (dragging || dropping) 1f else 0f)
            .offset(x = animatedPan.x.toDp(), y = animatedPan.y.toDp())
            .pointerInput(Unit) {
                coroutineScope {
                    while (true) {
                        awaitPointerEventScope {
                            awaitFirstDown()
                            boardInteraction.highlightMoves(state.square)
                            dragging = true
                            currentSize = size * PIECE_DRAG_SCALE

                            var event: PointerEvent
                            do {
                                event = awaitPointerEvent()
                                pan += event
                                    .calculatePan()
                                    .orZero()

                                val dragPosition = pan + state.squareOffset + Offset(size / 2f, size / 2f)
                                boardInteraction.updateDragPosition(dragPosition)
                            } while (event.changes.none { it.changedToUp() })

                            val dropResult = boardInteraction.dropPiece(state.piece, state.square)
                            if (dropResult is DropPieceResult.Moved) {
                                state.square = dropResult.to
                                state.squareOffset = state.square.topLeft(perspective, size)
                            }

                            boardInteraction.disableHighlightMoves()
                            pan = Offset.Zero
                            currentSize = size
                            dragging = false
                        }
                    }
                }
            },
        painter = painterResource(id = state.piece.drawableResource()),
        contentDescription = state.piece.name,
    )
}
