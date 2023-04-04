package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReusableContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.game.board.LegalMoves
import dev.mcd.chess.ui.game.board.PromotionSelector

@Composable
fun ChessBoard(
    modifier: Modifier = Modifier,
) {
    val boardInteraction = LocalBoardInteraction.current
    val perspective by boardInteraction.perspectiveChanges().collectAsState(boardInteraction.perspective())
    val boardLayout = rememberBoardLayout(perspective = perspective)

    LaunchedEffect(boardLayout) {
        boardInteraction.updateSquarePositions(boardLayout.squareSize)
    }

    ReusableContent(key = perspective) {
        Box(modifier = modifier) {
            boardLayout.run {
                Squares()
                MoveHighlight()
                TargetHighlight()
                LegalMoves()
                Pieces()
            }
            PromotionSelector(modifier = Modifier.align(Alignment.Center))
        }
    }
}
