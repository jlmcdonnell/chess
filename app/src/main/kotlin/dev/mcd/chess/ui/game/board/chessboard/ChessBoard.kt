package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReusableContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.game.board.LegalMoves
import dev.mcd.chess.ui.game.board.PromotionSelector

@Composable
fun ChessBoard(
    gameId: GameId,
    modifier: Modifier = Modifier,
) {
    val boardInteraction = LocalBoardInteraction.current
    var boardLayout by remember { mutableStateOf(BoardLayout()) }
    val perspective by boardInteraction.perspective().collectAsState(Side.WHITE)

    Box(
        modifier = modifier.calculateBoardLayout(perspective) {
            boardLayout = it
            boardInteraction.updateSquarePositions(boardLayout.squarePositions)
        },
    ) {
        ReusableContent(listOf(gameId, perspective)) {
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
