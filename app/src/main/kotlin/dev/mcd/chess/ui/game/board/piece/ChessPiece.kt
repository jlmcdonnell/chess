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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.zIndex
import dev.mcd.chess.common.game.extension.relevantToMove
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.extension.orZero
import dev.mcd.chess.ui.extension.toDp
import dev.mcd.chess.ui.game.board.chessboard.BoardLayout
import dev.mcd.chess.ui.game.board.interaction.DropPieceResult
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter

context(BoardLayout)
@Composable
fun ChessPiece(initialState: ChessPieceState) {
    val gameManager = LocalGameSession.current
    val boardInteraction = LocalBoardInteraction.current

    var currentSize by remember { mutableStateOf(squareSize) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var dragging by remember { mutableStateOf(false) }
    var state by remember { mutableStateOf(initialState) }

    LaunchedEffect(Unit) {
        gameManager.moveUpdates()
            .filter { (move, _) -> state.square.relevantToMove(move) }
            .collectLatest { directionalMove ->
                val moveCount = gameManager.moveCount() ?: 0
                state = UpdateChessPieceState(moveCount, directionalMove, state)
                pan = Offset.Zero
                currentSize = squareSize
            }
    }

    val animatedSize by animateDpAsState(currentSize.toDp(), label = "Piece Size")

    val position = remember(state, pan, dragging) {
        Offset(
            x = pan.x - (if (dragging) (animatedSize.value / 2f) else 0f) + state.squareOffset.x,
            y = pan.y - (if (dragging) (animatedSize.value * 2f) else 0f) + state.squareOffset.y,
        )
    }

    val animatedPan by animateOffsetAsState(
        targetValue = position,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "Piece Move",
    )

    val moving = animatedSize != currentSize.toDp() || animatedPan != position || dragging

    if (state.captured) {
        return
    }

    Image(
        modifier = Modifier
            .semantics {
                pieceSquare = SquarePieceTag(state.square, state.piece)
            }
            .size(animatedSize)
            .zIndex(if (moving) 1f else 0f)
            .offset(x = animatedPan.x.toDp(), y = animatedPan.y.toDp())
            .pointerInput(Unit) {
                coroutineScope {
                    while (true) {
                        awaitPointerEventScope {
                            awaitFirstDown()
                            boardInteraction.highlightMoves(state.square)
                            dragging = true
                            currentSize = squareSize * 1.7f

                            var event: PointerEvent
                            do {
                                event = awaitPointerEvent()
                                pan += event
                                    .calculatePan()
                                    .orZero()

                                val dragPosition = pan + state.squareOffset + Offset(squareSize / 2f, squareSize / 2f)
                                boardInteraction.updateDragPosition(dragPosition)
                            } while (event.changes.none { it.changedToUp() })

                            if (boardInteraction.dropPiece(state.piece, state.square) == DropPieceResult.None) {
                                pan = Offset.Zero
                                currentSize = squareSize
                                boardInteraction.clearHighlightMoves()
                            }
                            dragging = false
                        }
                    }
                }
            },
        painter = getPainter(state.piece),
        contentDescription = state.square.name,
    )
}
