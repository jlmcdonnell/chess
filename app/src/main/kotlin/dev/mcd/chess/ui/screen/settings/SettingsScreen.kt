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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.R
import dev.mcd.chess.feature.common.domain.AppColorScheme
import dev.mcd.chess.ui.game.board.chessboard.BoardLayout
import dev.mcd.chess.ui.game.board.chessboard.Squares
import dev.mcd.chess.ui.game.board.chessboard.calculateBoardLayout
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

        LazyColumn(
            Modifier
                .padding(padding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                ColorSchemeSelection(
                    colorScheme = state.colorScheme,
                    onColorSchemeChanged = { viewModel.updateColorScheme(it) },
                )
            }
            item {
                if (state.showDebug) {
                    Spacer(modifier = Modifier.height(24.dp))
                    DebugSettings(
                        debugModel = state.debugModel,
                        onUpdateHost = { viewModel.updateHost(it) },
                        onClearAuthData = { viewModel.clearAuthData() },
                    )
                }
            }
        }
    }
}

@Composable
fun ColorSchemeSelection(
    colorScheme: AppColorScheme,
    onColorSchemeChanged: (AppColorScheme) -> Unit,
) {
    var boardLayout by remember { mutableStateOf(BoardLayout()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(24.dp),
            text = stringResource(R.string.set_color_scheme),
        )
        LazyRow(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalArrangement = spacedBy(12.dp),
        ) {
            items(AppColorScheme.values()) { scheme ->
                ColorSchemeChip(
                    colorScheme = scheme,
                    isSelected = scheme == colorScheme,
                    onClick = { onColorSchemeChanged(scheme) },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedCard(
            modifier = Modifier
                .size(200.dp)
                .calculateBoardLayout(Side.WHITE) { boardLayout = it }
                .clip(MaterialTheme.shapes.extraLarge),
        ) {
            boardLayout.run {
                Squares(drawLabels = false)
            }
        }
    }
}

@Composable
fun ColorSchemeChip(
    colorScheme: AppColorScheme,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    ElevatedFilterChip(
        modifier = Modifier.height(48.dp),
        label = { Text(text = colorScheme.name) },
        selected = isSelected,
        onClick = onClick,
    )
}

@Composable
fun DebugSettings(
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
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
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
        horizontalArrangement = spacedBy(12.dp),
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
