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
import dev.mcd.chess.ui.extension.drawableResource
import dev.mcd.chess.ui.extension.toDp
import dev.mcd.chess.ui.extension.topLeft
import dev.mcd.chess.ui.game.board.LocalBoardInteraction
import dev.mcd.chess.ui.game.board.LocalGameSession
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

@Composable
fun ChessPiece2(
    size: Float,
    perspective: Side,
    initialPiece: Piece,
    initialSquare: Square,
) {
    val gameManager = LocalGameSession.current
    val boardInteraction = LocalBoardInteraction.current

    var currentSize by remember { mutableStateOf(size) }
    var squareOffset by remember { mutableStateOf(initialSquare.topLeft(perspective, size)) }
    var square by remember { mutableStateOf(initialSquare) }
    var piece by remember { mutableStateOf(initialPiece) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var dragging by remember { mutableStateOf(false) }
    var captured by remember { mutableStateOf(false) }

    LaunchedEffect(square) {
        gameManager.moveUpdates()
            .filter { it.from == square || it.to == square }
            .collectLatest {
                if (it.from == square) {
                    println("($square) MOVING TO: ${it.to}")
                    println("($square) OLD: $squareOffset NEW:${it.to.topLeft(perspective, size)}")
                    square = it.to
                    squareOffset = it.to.topLeft(perspective, size)
                }
            }
    }

    if (captured) return

    val animatedSize by animateDpAsState(currentSize.toDp())

    val tX: Float
    val tY: Float

    if (dragging) {
        tX = pan.x - animatedSize.value / 2f
        tY = pan.y - animatedSize.value * 2f
    } else {
        tX = pan.x
        tY = pan.y
    }

    val animatedPan by animateOffsetAsState(
        Offset(tX + squareOffset.x, tY + squareOffset.y),
        spring(
            visibilityThreshold = Offset.VisibilityThreshold,
            stiffness = if (dragging) Spring.StiffnessHigh else Spring.StiffnessMedium
        )
    )

    Image(
        modifier = Modifier
            .size(animatedSize)
            .zIndex(if (dragging) 1f else 0f)
            .graphicsLayer {
                translationX = animatedPan.x
                translationY = animatedPan.y
            }
            .pointerInput(squareOffset, square, piece) {
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown()
                        dragging = true
                        currentSize = size * 2
                        var event: PointerEvent
                        do {
                            event = awaitPointerEvent()
                            pan += event
                                .calculatePan()
                                .takeIf { it != Offset.Unspecified } ?: Offset.Zero
                            boardInteraction.updateDragPosition(pan+squareOffset+Offset(size/2, size/2))
                        } while (event.changes.none { it.changedToUp() })
                        currentSize = size
                        dragging = false
                        pan = Offset.Zero
                        boardInteraction.releaseTarget()
                    }
                }
            },
        painter = painterResource(id = piece.drawableResource()),
        contentDescription = "",
    )
}
