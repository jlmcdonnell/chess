package dev.mcd.chess.ui.game.board

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Square
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.extension.toDp
import dev.mcd.chess.ui.extension.topLeft
import dev.mcd.chess.ui.game.board.chessboard.BoardLayout
import dev.mcd.chess.ui.game.board.chessboard.BoardTheme

context(BoardLayout)
@Composable
fun LegalMoves() {
    val gameManager = LocalGameSession.current

    val highlightMovesFrom by LocalBoardInteraction.current
        .highlightMovesFrom()
        .collectAsState(Square.NONE)

    var highlightMoves by remember { mutableStateOf<List<Pair<Square, Boolean>>>(emptyList()) }

    LaunchedEffect(highlightMovesFrom) {
        val occupiedSquares = gameManager.pieces().mapIndexed { index, piece ->
            if (piece == Piece.NONE) {
                Square.NONE
            } else {
                Square.squareAt(index)
            }
        }.filter { it != Square.NONE }

        highlightMoves = if (highlightMovesFrom != Square.NONE) {
            gameManager.legalMoves()
                .filter { it.from == highlightMovesFrom }
                .map { it.to to (it.to in occupiedSquares) }
                .distinct()
        } else {
            emptyList()
        }
    }

    val legalMoveHighlight = BoardTheme.legalMoveHighlight

    ReusableContent(highlightMovesFrom) {
        highlightMoves.forEach { (square, pieceOnSquare) ->
            val offset = square.topLeft()
            if (pieceOnSquare) {
                Canvas(
                    modifier = Modifier
                        .offset(
                            x = offset.x.toDp(),
                            y = offset.y.toDp(),
                        )
                        .size(squareSize.toDp()),
                ) {
                    val border = size.width / 8f
                    drawArc(
                        color = legalMoveHighlight,
                        useCenter = true,
                        topLeft = Offset(border / 2, border / 2),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        style = Stroke(width = border, cap = StrokeCap.Round),
                        size = Size(size.width - border, size.height - border),
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .offset(
                            x = offset.x.toDp() + squareSize.toDp() / 4,
                            y = offset.y.toDp() + squareSize.toDp() / 4,
                        )
                        .clip(CircleShape)
                        .size(squareSize.toDp() / 2)
                        .background(legalMoveHighlight),
                )
            }
        }
    }
}
