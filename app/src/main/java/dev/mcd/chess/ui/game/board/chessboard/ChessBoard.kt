package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.foundation.layout.Box
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
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.extension.center
import dev.mcd.chess.ui.game.board.LegalMoves
import dev.mcd.chess.ui.game.board.PromotionSelector

@Composable
fun ChessBoard(
    gameId: GameId,
    modifier: Modifier = Modifier,
) {
    var boardLayout by remember { mutableStateOf(BoardLayout()) }
    val boardInteraction = LocalBoardInteraction.current
    val perspective by boardInteraction.perspective().collectAsState(Side.WHITE)

    LaunchedEffect(boardLayout) {
        val squarePositions = Square.values().associateWith { square ->
            boardLayout.run { square.center() }
        }
        boardInteraction.updateSquarePositions(squarePositions)
    }

    Box(
        modifier = modifier.boardLayout(perspective) { boardLayout = it },
    ) {
        ReusableContent(gameId) {
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
