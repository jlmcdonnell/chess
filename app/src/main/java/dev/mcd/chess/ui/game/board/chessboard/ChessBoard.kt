package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.LocalBoardTheme
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.extension.center
import dev.mcd.chess.ui.extension.toDp
import dev.mcd.chess.ui.extension.toPx
import dev.mcd.chess.ui.extension.topLeft
import dev.mcd.chess.ui.game.board.LegalMoves
import dev.mcd.chess.ui.game.board.PromotionSelector
import dev.mcd.chess.ui.game.board.piece.ChessPiece
import dev.mcd.chess.ui.game.board.piece.ChessPieceState

@Composable
fun ChessBoard(
    gameId: GameId,
    modifier: Modifier = Modifier,
) {
    var squareSizeDp by remember { mutableStateOf(0.dp) }
    var squareSize by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val boardInteraction = LocalBoardInteraction.current
    val perspective by boardInteraction.perspective().collectAsState(Side.WHITE)

    LaunchedEffect(perspective, squareSize) {
        val squarePositions = Square.values().associateWith { square -> square.center(perspective, squareSize) }
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
        ReusableContent(gameId) {
            Squares(perspective, squareSize)
            MoveHighlight(perspective, squareSizeDp)
            TargetHighlight(perspective, squareSizeDp)
            LegalMoves(perspective, squareSize)
            Pieces(perspective, squareSize)
            PromotionSelector(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun TargetHighlight(
    perspective: Side,
    squareSize: Dp,
) {
    val boardTheme = LocalBoardTheme.current
    val squareSizePx = squareSize.toPx()
    val target by LocalBoardInteraction.current.targets().collectAsState(Square.NONE)

    if (target != Square.NONE) {
        val offset by animateOffsetAsState(
            targetValue = target
                .topLeft(perspective, squareSizePx)
                .minus(Offset(squareSizePx / 2, squareSizePx / 2)),
            animationSpec = spring(stiffness = Spring.StiffnessHigh),
            label = "Target Highlight",
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

    Canvas(Modifier.fillMaxSize().semantics { contentDescription = "Board" }) {
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
    val game by LocalGameSession.current.sessionUpdates().collectAsState(null)
    val pieces = remember(game?.id) {
        game?.pieceUpdates()?.value ?: emptyList()
    }
    pieces.forEachIndexed { index, piece ->
        if (piece != Piece.NONE) {
            val initialSquare = Square.squareAt(index)
            ChessPiece(
                perspective = perspective,
                size = squareSize,
                initialState = ChessPieceState(
                    square = initialSquare,
                    squareOffset = initialSquare.topLeft(perspective, squareSize),
                    piece = piece,
                    captured = false,
                ),
            )
        }
    }
}
