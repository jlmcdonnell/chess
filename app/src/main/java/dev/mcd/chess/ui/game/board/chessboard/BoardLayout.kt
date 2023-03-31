package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Side

data class BoardLayout(
    var squareSizeDp: Dp = 0.dp,
    var squareSize: Float = 0f,
    val perspective: Side = Side.WHITE,
) {
    val isWhite get() = perspective == Side.WHITE
}
