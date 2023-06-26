package dev.mcd.chess.ui.game.board.piece

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import dev.mcd.chess.common.game.extension.relevantToMove
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.extension.orZero
import dev.mcd.chess.ui.extension.toDp
import dev.mcd.chess.ui.game.board.chessboard.BoardLayout
import dev.mcd.chess.ui.game.board.interaction.DropPieceResult
import dev.mcd.chess.ui.theme.ChessTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

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

    val position = remember(state, pan, dragging, animatedSize) {
        val sizeOffsetX = if (dragging) (animatedSize.value / 2f) else 0f
        val sizeOffsetY = if (dragging) (animatedSize.value * 2f) else 0f

        Offset(
            x = pan.x + state.squareOffset.x - sizeOffsetX,
            y = pan.y + state.squareOffset.y - sizeOffsetY,
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
                            val pointer = awaitFirstDown()
                            val newSize = squareSize * 1.7f
                            val squareCenter = Offset(squareSize / 2f, squareSize / 2f)

                            pan += Offset(
                                x = -(squareCenter.x - pointer.position.x),
                                y = -(squareCenter.y - pointer.position.y),
                            )

                            boardInteraction.highlightMoves(state.square)
                            boardInteraction.updateDragPosition(pan + state.position)

                            currentSize = newSize
                            dragging = true

                            do {
                                val event = awaitPointerEvent()

                                pan += event
                                    .calculatePan()
                                    .orZero()

                                val dragPosition = pan + state.squareOffset + squareCenter
                                boardInteraction.updateDragPosition(dragPosition)
                            } while (event.changes.none { it.changedToUp() })

                            when (val result = boardInteraction.dropPiece(state.piece, state.square)) {
                                DropPieceResult.None -> {
                                    pan = Offset.Zero
                                    currentSize = squareSize
                                    dragging = false
                                }
                                is DropPieceResult.SelectPromotion -> {
                                    launch {
                                        val promoted = boardInteraction.selectPromotion(result.promotions)
                                        if (!promoted) {
                                            pan = Offset.Zero
                                            currentSize = squareSize
                                        }
                                        dragging = false
                                    }
                                }
                                else -> {
                                    dragging = false
                                }
                            }
                        }
                    }
                }
            },
        painter = getPainter(state.piece),
        contentDescription = state.square.name,
    )
}
