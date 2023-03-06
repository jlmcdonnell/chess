import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.ui.extension.drawableResource
import dev.mcd.chess.ui.extension.orZero
import dev.mcd.chess.ui.extension.toDp
import dev.mcd.chess.ui.extension.topLeft
import dev.mcd.chess.ui.game.board.LocalBoardInteraction
import dev.mcd.chess.ui.game.board.LocalGameSession
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull

private const val PIECE_SIZE_MULT_WHEN_DRAGGED = 2

@Composable
fun ChessPiece(
    size: Float,
    perspective: Side,
    side: Side,
    initialPiece: Piece,
    initialSquare: Square,
) {
    var captured by remember { mutableStateOf(false) }
    if (captured) return

    val gameManager = LocalGameSession.current
    val boardInteraction = LocalBoardInteraction.current

    var currentSize by remember { mutableStateOf(size) }
    var squareOffset by remember { mutableStateOf(initialSquare.topLeft(perspective, size)) }
    var square by remember { mutableStateOf(initialSquare) }
    var piece by remember { mutableStateOf(initialPiece) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var dragging by remember { mutableStateOf(false) }

    LaunchedEffect(square) {
        gameManager
            .moveUpdates()
            .mapNotNull { gameManager.lastMove() }
            .filter {
                square in listOf(
                    it.move.from,
                    it.move.to,
                    it.rookCastleMove?.from,
                    it.enPassantTarget
                )
            }
            .collectLatest { backup ->
                val move = backup.move
                if (move.from == square) {
                    square = move.to
                    squareOffset = move.to.topLeft(perspective, size)
                    if (move.promotion != Piece.NONE) {
                        piece = move.promotion
                    }
                } else if (piece == backup.capturedPiece && square == backup.capturedSquare) {
                    captured = true
                } else if (backup.rookCastleMove?.from == square) {
                    square = backup.rookCastleMove.to
                    squareOffset = square.topLeft(perspective, size)
                } else if (backup.enPassantTarget == square) {
                    square = backup.enPassantTarget
                    squareOffset = square.topLeft(perspective, size)
                }
            }
    }

    val animatedSize by animateDpAsState(currentSize.toDp())

    val position = if (dragging) {
        Offset(
            x = (pan.x - animatedSize.value / 2f) + squareOffset.x,
            y = (pan.y - animatedSize.value * 2f) + squareOffset.y,
        )
    } else {
        Offset(
            x = pan.x + squareOffset.x,
            y = pan.y + squareOffset.y,
        )
    }

    val dropping = animatedSize != currentSize.toDp()
    val animatedPan by animateOffsetAsState(
        targetValue = position,
        animationSpec = spring(
            visibilityThreshold = Offset.VisibilityThreshold,
            stiffness = if (dragging && dropping) Spring.StiffnessHigh else Spring.StiffnessMedium
        )
    )

    Image(
        modifier = Modifier
            .size(animatedSize)
            .zIndex(if (dragging || dropping) 1f else 0f)
            .graphicsLayer {
                translationX = animatedPan.x
                translationY = animatedPan.y
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown()
                        boardInteraction.highlightMoves(square)
                        dragging = true
                        currentSize = size * PIECE_SIZE_MULT_WHEN_DRAGGED

                        var event: PointerEvent
                        do {
                            event = awaitPointerEvent()
                            pan += event
                                .calculatePan()
                                .orZero()

                            val dragPosition = pan + squareOffset + Offset(size / 2f, size / 2f)
                            boardInteraction.updateDragPosition(dragPosition)
                        } while (event.changes.none { it.changedToUp() })

                        if (side == piece.pieceSide) {
                            val target = boardInteraction.target
                            val move = Move(square, target)
                            val promotions = gameManager.promotions(move)

                            if (move in gameManager.legalMoves()) {
                                if (boardInteraction.placePieceFrom(square)) {
                                    square = target
                                    squareOffset = square.topLeft(perspective, size)
                                }
                                boardInteraction.releaseTarget()
                            } else if (promotions.isNotEmpty()) {
                                boardInteraction.selectPromotion(promotions)
                            } else {
                                boardInteraction.releaseTarget()
                            }
                        } else {
                            boardInteraction.releaseTarget()
                        }

                        boardInteraction.disableHighlightMoves()
                        pan = Offset.Zero
                        currentSize = size
                        dragging = false
                    }
                }
            },
        painter = painterResource(id = piece.drawableResource()),
        contentDescription = piece.name,
    )
}
