@file:OptIn(ExperimentalMaterial3Api::class)

package dev.mcd.chess.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.mcd.chess.R
import dev.mcd.chess.ui.settings.BoardSettings
import dev.mcd.chess.ui.settings.ColorSchemeSelection
import dev.mcd.chess.ui.settings.DebugSettings
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { onDismiss() }) {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.Rounded.ArrowBack),
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        val state by viewModel.collectAsState()

        Column(
            Modifier
                .padding(padding)
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ColorSchemeSelection(
                colorScheme = state.colorScheme,
                onColorSchemeChanged = { viewModel.setColorScheme(it) },
            )
            BoardSettings(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                soundsEnabled = state.soundsEnabled,
                onSoundsEnabledChanged = viewModel::setSoundsEnabled,
            )
            if (state.showDebug) {
                Spacer(modifier = Modifier.height(24.dp))
                DebugSettings(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    debugModel = state.debugModel,
                    onUpdateHost = { viewModel.setHost(it) },
                    onClearAuthData = { viewModel.clearAuthData() },
                )
            }
        }
    }
}
