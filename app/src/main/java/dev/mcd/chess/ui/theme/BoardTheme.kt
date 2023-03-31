package dev.mcd.chess.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

data class BoardTheme(
    val squareDark: Color,
    val squareLight: Color,
    val moveHint: Color,
    val targetSquareHighlight: Color,
    val lastMoveHighlight: Color,
    val legalMoveHighlight: Color,
    val squareTextStyle: TextStyle,
)

val defaultBoardTheme = BoardTheme(
    squareDark = Color(0xFF547296),
    squareLight = Color(0xFFEAE9D4),
    moveHint = Color(0x6AE8FF00),
    targetSquareHighlight = Color(0x28000000),
    legalMoveHighlight = Color(0x28000000),
    lastMoveHighlight = Color(0x72379AFF),
    squareTextStyle = TextStyle(
        color = Color.Unspecified,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 11.sp,
    ),
)
