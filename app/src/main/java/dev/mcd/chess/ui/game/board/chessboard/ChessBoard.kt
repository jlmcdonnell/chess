package dev.mcd.chess.ui.game.board.chessboard

import ChessPiece
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReusableContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.LocalBoardTheme
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.extension.center
import dev.mcd.chess.ui.extension.toDp
import dev.mcd.chess.ui.extension.toPx
import dev.mcd.chess.ui.extension.topLeft
import dev.mcd.chess.ui.game.board.LegalMoves
import dev.mcd.chess.ui.game.board.PromotionSelector
import kotlinx.coroutines.flow.collectLatest
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun ChessBoard(
    modifier: Modifier = Modifier,
    viewModel: ChessBoardViewModel = hiltViewModel(),
    onMove: (Move) -> Unit = {},
) {
    var squareSizeDp by remember { mutableStateOf(0.dp) }
    var squareSize by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val boardInteraction = LocalBoardInteraction.current
    val game by LocalGameSession.current.sessionUpdates().collectAsState(null)
    val perspective by boardInteraction.perspective().collectAsState(Side.WHITE)

    viewModel.collectAsState()

    LaunchedEffect(Unit) {
        boardInteraction.moves().collectLatest {
            onMove(it)
        }
    }

    LaunchedEffect(game) {
        boardInteraction.session = game
    }

    LaunchedEffect(perspective, squareSize) {
        val squarePositions =
            Square.values().associateWith { square -> square.center(perspective, squareSize) }
        boardInteraction.updateSquareData(squarePositions, squareSize)
    }

    Box(
        modifier = modifier.onGloballyPositioned {
            density.run {
                squareSize = it.size.width.div(8).toFloat()
                squareSizeDp = squareSize.toDp()
            }
        }
    ) {
        ReusableContent(game?.id ?: "") {
            Squares(perspective, squareSize)
            SquareHighlight(perspective, squareSizeDp)
            LegalMoves(perspective, squareSize)
            Pieces(perspective, squareSize)
            PromotionSelector(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun SquareHighlight(
    perspective: Side,
    squareSize: Dp,
) {
    val boardTheme = LocalBoardTheme.current
    val targetSquare by LocalBoardInteraction.current.targets().collectAsState(Square.NONE)
    val lastMove by LocalGameSession.current.moveUpdates().collectAsState(null)
    val squareSizePx = squareSize.toPx()

    lastMove?.move?.let { move ->
        val moveFromOffset = move.from.topLeft(perspective, squareSizePx)
        val moveToOffset = move.to.topLeft(perspective, squareSizePx)
        Box(
            modifier = Modifier
                .size(squareSize)
                .offset(moveFromOffset.x.toDp(), moveFromOffset.y.toDp())
                .background(boardTheme.lastMoveHighlight)
        )
        Box(
            modifier = Modifier
                .size(squareSize)
                .offset(moveToOffset.x.toDp(), moveToOffset.y.toDp())
                .background(boardTheme.lastMoveHighlight)
        )
    }

    if (targetSquare != Square.NONE) {
        val offset by animateOffsetAsState(
            targetValue = targetSquare
                .topLeft(perspective, squareSizePx)
                .minus(Offset(squareSizePx / 2, squareSizePx / 2)),
            animationSpec = spring(stiffness = Spring.StiffnessHigh)
        )

        Box(
            modifier = Modifier
                .offset(offset.x.toDp(), offset.y.toDp())
                .size(squareSize * 2)
                .clip(CircleShape)
                .background(boardTheme.targetSquareHighlight)
        )
    }
}

@Composable
private fun Squares(
    perspective: Side,
    squareSize: Float,
) {
    val darkSquareColor = LocalBoardTheme.current.squareDark
    val lightSquareColor = LocalBoardTheme.current.squareLight

    Canvas(Modifier) {
        for (square in Square.values()) {
            if (square != Square.NONE) {
                val color = if (square.isLightSquare) lightSquareColor else darkSquareColor
                drawRect(
                    color = color,
                    topLeft = square.topLeft(perspective, squareSize),
                    size = Size(squareSize, squareSize)
                )
            }
        }
    }
}

@Composable
private fun Pieces(
    perspective: Side,
    squareSize: Float,
) {
    val session = LocalGameSession.current
    val game by session.sessionUpdates().collectAsState(null)
    var pieces by remember { mutableStateOf(emptyList<Piece>()) }

    ReusableContent(game?.id ?: "") {
        pieces = game?.pieceUpdates()?.value ?: return

        pieces.forEachIndexed { index, piece ->
            if (piece != Piece.NONE) {
                ChessPiece(
                    initialSquare = Square.squareAt(index),
                    initialPiece = piece,
                    perspective = perspective,
                    size = squareSize,
                )
            }
        }
    }
}
