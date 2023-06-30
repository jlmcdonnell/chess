package dev.mcd.chess.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import dev.mcd.chess.R

val Typography: Typography
    @Composable
    get() {
        val font = FontFamily(Font(R.font.inter))
        with(MaterialTheme.typography) {
            return Typography(
                displayLarge = displayLarge.copy(fontFamily = font),
                displayMedium = displayMedium.copy(fontFamily = font),
                displaySmall = displaySmall.copy(fontFamily = font),
                headlineLarge = headlineLarge.copy(fontFamily = font),
                headlineMedium = headlineMedium.copy(fontFamily = font),
                headlineSmall = headlineSmall.copy(fontFamily = font),
                titleLarge = titleLarge.copy(fontFamily = font),
                titleMedium = titleMedium.copy(fontFamily = font),
                titleSmall = titleSmall.copy(fontFamily = font),
                bodyLarge = bodyLarge.copy(fontFamily = font),
                bodyMedium = bodyMedium.copy(fontFamily = font),
                bodySmall = bodySmall.copy(fontFamily = font),
                labelLarge = labelLarge.copy(fontFamily = font),
                labelMedium = labelMedium.copy(fontFamily = font),
                labelSmall = labelSmall.copy(fontFamily = font),
            )
        }
    }
