import androidx.compose.animation.core.Spring
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
import androidx.compose.ui.semantics.contentDescription
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
import dev.mcd.chess.ui.game.board.piece.GetMoveOutputs
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapNotNull

private const val PIECE_DRAG_SCALE = 1.7f

@Composable
fun ChessPiece(
    size: Float,
    perspective: Side,
    initialPiece: Piece,
    initialSquare: Square,
) {
    var captured by remember { mutableStateOf(false) }

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
            .relevantToSquare(square)
            .collectLatest { directionalMove ->
                val output = GetMoveOutputs(perspective, size, directionalMove, piece, captured, square)
                with(output) {
                    square = newSquare
                    piece = newPiece
                    captured = newCaptured
                    squareOffset = newSquareOffset
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
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )

    if (captured) {
        return
    }

    Image(
        modifier = Modifier
            .semantics { contentDescription = square.toString() }
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
                        currentSize = size * PIECE_DRAG_SCALE

                        var event: PointerEvent
                        do {
                            event = awaitPointerEvent()
                            pan += event
                                .calculatePan()
                                .orZero()

                            val dragPosition = pan + squareOffset + Offset(size / 2f, size / 2f)
                            boardInteraction.updateDragPosition(dragPosition)
                        } while (event.changes.none { it.changedToUp() })

                        val dropResult = boardInteraction.dropPiece(piece, square)
                        if (dropResult is DropPieceResult.Moved) {
                            square = dropResult.to
                            squareOffset = square.topLeft(perspective, size)
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
