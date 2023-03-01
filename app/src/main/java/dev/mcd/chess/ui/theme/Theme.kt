package dev.mcd.chess.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

class AppColors(
    val green: Color = Color.Transparent,
    val neutralPieceBackground: Color = Color.Transparent
)

val LocalAppColors = compositionLocalOf { AppColors() }

private val DarkColorPalette = darkColors(
    primary = Color(0xFF00E9A6),
    primaryVariant = Color(0xFF008F66),
    secondary = Color(0xFF03DAC5)
)

private val DefaultAppColors = AppColors(
    green = Color(0xFF00FF37),
    neutralPieceBackground = Color(0xFF26324B),
)

@Composable
fun ChessTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppColors provides DefaultAppColors) {
        MaterialTheme(
            colors = DarkColorPalette,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
