package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side

@Stable
data class BoardLayout(
    val squareSizeDp: Dp = 0.dp,
    val squareSize: Float = 0f,
    val perspective: Side = Side.WHITE,
    val getPainter: (Piece) -> Painter,
) {
    val isWhite get() = perspective == Side.WHITE
}
