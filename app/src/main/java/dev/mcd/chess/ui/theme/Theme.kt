package dev.mcd.chess.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import dev.mcd.chess.ui.LocalAppColors

class AppColors(
    val green: Color = Color.Transparent,
    val neutralPieceBackground: Color = Color.Transparent,
    val profileImageBackground: Color = Color.Transparent,
    val profileImageForeground: Color = Color.Transparent,
)

private val darkColorPalette = darkColorScheme(
    primary = Color(0xFF00E9A6),
    primaryContainer = Color(0xFF008F66),
    secondary = Color(0xFF03DAC5),
    background = Color(0xFF2E3D5A),
    surface = Color(0xFF2E3D5A),
)

private val defaultAppColors = AppColors(
    green = Color(0xFF00FF37),
    neutralPieceBackground = Color(0xFF26324B),
    profileImageBackground = Color(0xFF27334B),
    profileImageForeground = Color(0xFFBEFFED),
)

@Composable
fun ChessTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalAppColors provides defaultAppColors) {
        MaterialTheme(
            colorScheme = darkColorPalette,
            typography = Typography,
            shapes = Shapes,
            content = content,
        )
    }
}
