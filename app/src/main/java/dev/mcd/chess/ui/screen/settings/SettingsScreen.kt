@file:OptIn(ExperimentalMaterial3Api::class)

package dev.mcd.chess.ui.screen.settings

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
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
                },
            )
        },
    ) { padding ->

        val state by viewModel.collectAsState()

        Column(
            Modifier
                .padding(padding)
                .fillMaxWidth(),
        ) {
            if (state.showDebug) {
                DebugSettings(
                    prefillHosts = state.prefillHosts,
                    currentHost = state.host,
                    onUpdateHost = { viewModel.updateHost(it) },
                    onClearAuthData = { viewModel.clearAuthData() },
                )
            }
        }
    }
}

@Composable
fun DebugSettings(
    prefillHosts: List<String>,
    currentHost: String,
    onUpdateHost: (String) -> Unit,
    onClearAuthData: () -> Unit,
) {
    var debugApiHost by remember {
        mutableStateOf(TextFieldValue(text = currentHost))
    }

    val context = LocalContext.current

    val restart = {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)!!
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    OutlinedTextField(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        value = debugApiHost,
        onValueChange = {
            onUpdateHost(it.text)
            debugApiHost = it
        },
        label = { Text(text = "API Host") },
        trailingIcon = {
            IconButton(onClick = { debugApiHost = debugApiHost.copy(text = "") }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.Clear),
                    contentDescription = "Clear",
                )
            }
        },
    )
    LazyRow(
        modifier = Modifier.padding(horizontal = 24.dp),
        horizontalArrangement = spacedBy(12.dp),
    ) {
        items(prefillHosts) { host ->
            HostChip(
                host = host,
                onClick = {
                    debugApiHost = TextFieldValue(text = host)
                    onUpdateHost(host)
                },
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
    LazyRow(
        Modifier
            .padding(24.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        item {
            TextButton(
                onClick = {
                    restart()
                },
            ) {
                Text(text = "Restart")
            }
        }
        item {
            TextButton(
                onClick = { onClearAuthData() },
            ) {
                Text(text = "Clear Auth Data")
            }
        }
    }
}

@Composable
fun HostChip(
    host: String,
    onClick: () -> Unit,
) {
    ElevatedAssistChip(
        onClick = onClick,
        label = { Text(text = host) },
    )
}
