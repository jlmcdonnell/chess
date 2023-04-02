package dev.mcd.chess.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import dev.mcd.chess.feature.common.domain.AppColorScheme

sealed interface ComposeAppColorScheme {

    data class BoardColors(
        val squareDark: Color,
        val squareLight: Color,
        val lastMoveHighlightOnDark: Color,
        val lastMoveHighlightOnLight: Color,
        val targetSquareHighlight: Color,
        val legalMoveHighlight: Color,
    )

    val materialColorScheme: ColorScheme
    val boardColors: BoardColors

    object Brown : ComposeAppColorScheme {
        override val boardColors: BoardColors
            get() = BoardColors(
                lastMoveHighlightOnDark = Color(0x4AD8B449),
                lastMoveHighlightOnLight = Color(0x4AD8B449),
                targetSquareHighlight = Color(0x28000000),
                legalMoveHighlight = Color(0x28000000),
                squareDark = materialColorScheme.tertiaryContainer,
                squareLight = materialColorScheme.tertiary,
            )

        override val materialColorScheme = darkColorScheme(
            primary = Color(0xFFE2C3B4),
            onPrimary = Color(0xFF000000),
            primaryContainer = Color.Red,
            onPrimaryContainer = Color.Red,
            secondary = Color.Red,
            onSecondary = Color.Red,
            secondaryContainer = Color(0xFF8B6961),
            onSecondaryContainer = Color(0xFFFFE0CF),
            tertiary = Color(0xFFD6C2B5),
            onTertiary = Color(0xFF583626),
            tertiaryContainer = Color(0xFF8B6961),
            onTertiaryContainer = Color(0xFFFFE0CF),
            error = Color(0xFFFFB4AB),
            errorContainer = Color(0xFF93000A),
            onError = Color(0xFF690005),
            onErrorContainer = Color(0xFFFFDAD6),
            background = Color(0xFF352923),
            onBackground = Color(0xFFE2C3B4),
            surface = Color(0xFF352923),
            onSurface = Color(0xFFE2C3B4),
            surfaceVariant = Color(0xFF5F4639),
            onSurfaceVariant = Color(0xFFB4998B),
            outline = Color(0xFF899391),
            surfaceTint = Color(0xFFD9DDDD),
            outlineVariant = Color(0xFF3F4947),
            scrim = Color(0xFF000000),
        )
    }

    object Blue : ComposeAppColorScheme {
        override val materialColorScheme: ColorScheme
            get() = Brown.materialColorScheme.copy(
                primary = Color(0xFFB4CCE2),
                onPrimary = Color(0xFF000000),
                secondaryContainer = Color(0xFF61698B),
                onSecondaryContainer = Color(0xFFCFE0FF),
                tertiary = Color(0xFFB5C2D6),
                onTertiary = Color(0xFF362658),
                tertiaryContainer = Color(0xFF61698B),
                onTertiaryContainer = Color(0xFFCFE0FF),
                onErrorContainer = Color(0xFFD6DAFF),
                background = Color(0xFF23293D),
                onBackground = Color(0xFFC0D2E2),
                surface = Color(0xFF23293D),
                onSurface = Color(0xFFC0D2E2),
                surfaceVariant = Color(0xFF39465F),
                onSurfaceVariant = Color(0xFFC0D2E2),
            )

        override val boardColors: BoardColors
            get() = Brown.boardColors.copy(
                lastMoveHighlightOnDark = Color(0x4A49D3D8),
                lastMoveHighlightOnLight = Color(0x4A49D3D8),
                squareDark = materialColorScheme.tertiaryContainer,
                squareLight = materialColorScheme.tertiary,
            )
    }

    companion object {
        fun fromAppColorScheme(appColorScheme: AppColorScheme): ComposeAppColorScheme {
            return when (appColorScheme) {
                AppColorScheme.Blue -> Blue
                AppColorScheme.Brown -> Brown
            }
        }
    }
}
