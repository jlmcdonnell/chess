package dev.mcd.chess.ui.theme.preferencestheme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import dev.mcd.chess.ui.LocalAppColors
import dev.mcd.chess.ui.theme.ComposeAppColorScheme
import dev.mcd.chess.ui.theme.Typography
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun PreferencesTheme(
    viewModel: PreferencesThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {
    val state by viewModel.collectAsState()

    state.colorScheme?.let { scheme ->
        val composeScheme = ComposeAppColorScheme.fromAppColorScheme(scheme)
        CompositionLocalProvider(LocalAppColors provides composeScheme) {
            MaterialTheme(
                colorScheme = composeScheme.materialColorScheme,
                typography = Typography,
                content = content,
            )
        }
    }
}
