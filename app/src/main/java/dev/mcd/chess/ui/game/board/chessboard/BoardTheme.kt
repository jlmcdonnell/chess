package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object BoardTheme {
    val squareDark @Composable get() = MaterialTheme.colorScheme.tertiaryContainer
    val squareLight @Composable get() = MaterialTheme.colorScheme.tertiary
    val lastMoveHighlightOnDark @Composable get() = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = .2f)
    val lastMoveHighlightOnLight @Composable get() = MaterialTheme.colorScheme.onTertiary.copy(alpha = .3f)
    val targetSquareHighlight = Color(0x28000000)
    val legalMoveHighlight = Color(0x28000000)

    val squareTextStyle: TextStyle
        @Composable get() = TextStyle(
            color = Color.Unspecified,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 11.sp,
        )
}
