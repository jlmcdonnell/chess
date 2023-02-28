@file:OptIn(ExperimentalMaterialApi::class)

package dev.mcd.chess.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.BuildConfig
import dev.mcd.chess.domain.api.DebugHostStore
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun SettingsScreen(
    onDismiss: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = { onDismiss() }) {
                        Icon(painter = rememberVectorPainter(image = Icons.Rounded.ArrowBack), contentDescription = "Back")
                    }
                }
            )
        },
    ) { padding ->
        var debugApiHost by remember { mutableStateOf(TextFieldValue()) }
        val viewModel = hiltViewModel<SettingsViewModel>()

        LaunchedEffect(debugApiHost) {
            if (debugApiHost.text.isNotEmpty()) {
                viewModel.updateHost(debugApiHost.text)
            } else {
                val host = viewModel.currentHost()
                debugApiHost = debugApiHost.copy(text = host)
            }
        }

        if (BuildConfig.DEBUG) {
            Column(Modifier.padding(padding).fillMaxWidth()) {
                OutlinedTextField(
                    modifier = Modifier.padding(24.dp),
                    value = debugApiHost,
                    onValueChange = { debugApiHost = it },
                    label = { Text(text = "API Host") },
                )
                Chip(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    onClick = { debugApiHost = debugApiHost.copy(text = "https://chess.mcd.dev") },
                ) {
                    Text(text = "https://chess.mcd.dev")
                }
            }
        }
    }
}

@HiltViewModel
private class SettingsViewModel @Inject constructor(private val debugHostStore: DebugHostStore) : ViewModel() {

    suspend fun currentHost(): String = debugHostStore.host()

    fun updateHost(host: String) {
        viewModelScope.launch {
            debugHostStore.setHost(host)
        }
    }

}
