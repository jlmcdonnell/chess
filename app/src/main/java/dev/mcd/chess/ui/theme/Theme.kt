package dev.mcd.chess.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

class AppColors(val green: Color = Color.Transparent)

val LocalAppColors = compositionLocalOf { AppColors() }

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val DefaultAppColors = AppColors(
    green = Color(0xFF00FF37)
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
