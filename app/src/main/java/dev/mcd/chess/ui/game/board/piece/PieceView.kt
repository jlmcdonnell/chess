package dev.mcd.chess.ui.game.board.piece

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.domain.chesslib.promotions
import dev.mcd.chess.ui.extension.drawableResource
import dev.mcd.chess.ui.extension.toDp
import dev.mcd.chess.ui.extension.toPx
import dev.mcd.chess.ui.extension.topLeft
import dev.mcd.chess.ui.game.board.LocalBoardInteraction
import dev.mcd.chess.ui.game.board.LocalGameSession

@Composable
fun PieceView(
    modifier: Modifier = Modifier,
    perspective: Side,
    square: Square,
    squareSize: Float,
    piece: Piece,
) {
    if (piece == Piece.NONE) {
        return
    }

    var positionInParent by remember { mutableStateOf(Offset.Zero) }
    var newPosition by remember { mutableStateOf<Offset?>(null) }
    var dragging: Boolean by remember { mutableStateOf(false) }
    var offset: Offset by remember { mutableStateOf(Offset.Zero) }

    val game by LocalGameSession.current.sessionUpdates.collectAsState()
    val boardInteraction = LocalBoardInteraction.current
    val target by boardInteraction.targets().collectAsState(Square.NONE)
    val visualDragOffsetY by animateFloatAsState(if (dragging) -50.dp.toPx() else 0f)
    val squarePosition = square.topLeft(perspective, squareSize)
    val finalOffset = (newPosition ?: squarePosition) + offset

    Image(
        modifier = modifier
            .size(squareSize.toDp())
            .offset(finalOffset.x.toDp(), finalOffset.y.toDp())
            .zIndex(if (dragging) 1f else 0f)
            .onGloballyPositioned {
                positionInParent = it.positionInParent()
            }
            .pointerInput(squareSize, square, piece) {
                detectDragGestures(onDrag = { change, dragAmount ->
                    if (dragAmount != Offset.Unspecified) {
                        offset = Offset(
                            x = offset.x + dragAmount.x + (change.position.x - squareSize / 2),
                            y = offset.y + dragAmount.y + (change.position.y + visualDragOffsetY - squareSize / 2),
                        )
                        val positionCentered = Offset(
                            x = positionInParent.x + squareSize / 2,
                            y = positionInParent.y - visualDragOffsetY + squareSize / 2
                        )
                        boardInteraction.updateDragPosition(positionCentered)
                    }
                }, onDragStart = {
                    dragging = true
                }, onDragEnd = {
                    val move = Move(square, target)
                    val legalMoves = game?.board?.legalMoves() ?: emptyList()
                    val canMove = move in legalMoves
                    val promotions = game?.board?.promotions(move) ?: emptyList()

                    if (canMove) {
                        boardInteraction.placePieceFrom(square)
                        boardInteraction.releaseTarget()
                        newPosition = target.topLeft(perspective, squareSize)
                    } else if (promotions.isNotEmpty()) {
                        boardInteraction.selectPromotion(promotions)
                    } else {
                        boardInteraction.releaseTarget()
                    }
                    dragging = false
                    offset = Offset.Zero
                }, onDragCancel = {
                    dragging = false
                    offset = Offset.Zero
                    boardInteraction.releaseTarget()
                })
            },
        painter = painterResource(piece.drawableResource()),
        contentDescription = piece.name,
    )
}

