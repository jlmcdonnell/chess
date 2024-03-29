package dev.mcd.chess.ui.settings

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import dev.mcd.chess.R
import dev.mcd.chess.ui.screen.settings.SettingsViewModel

@Composable
fun DebugSettings(
    modifier: Modifier = Modifier,
    debugModel: SettingsViewModel.DebugModel,
    onUpdateHost: (String) -> Unit,
    onClearAuthData: () -> Unit,
) {
    var debugApiHost by remember {
        mutableStateOf(TextFieldValue(text = debugModel.host))
    }

    val context = LocalContext.current

    val restart = {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)!!
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    OutlinedTextField(
        modifier = modifier,
        value = debugApiHost,
        onValueChange = {
            onUpdateHost(it.text)
            debugApiHost = it
        },
        label = { Text(text = stringResource(R.string.api_host)) },
        trailingIcon = {
            IconButton(onClick = { debugApiHost = debugApiHost.copy(text = "") }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Rounded.Clear),
                    contentDescription = stringResource(R.string.clear),
                )
            }
        },
    )
    LazyRow(
        modifier = Modifier.padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Absolute.spacedBy(12.dp),
    ) {
        items(debugModel.prefillHosts) { host ->
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
                Text(text = stringResource(R.string.restart))
            }
        }
        item {
            TextButton(
                onClick = { onClearAuthData() },
            ) {
                Text(text = stringResource(R.string.clear_auth_data))
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
