package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square

data class BoardLayout(
    val squareSizeDp: Dp = 0.dp,
    val squareSize: Float = 0f,
    val perspective: Side = Side.WHITE,
    val squarePositions: Map<Square, Offset> = emptyMap(),
) {
    val isWhite get() = perspective == Side.WHITE
}
