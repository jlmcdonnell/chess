package dev.mcd.chess.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.mcd.chess.R

@Composable
fun BoardSettings(
    modifier: Modifier = Modifier,
    soundsEnabled: Boolean,
    onSoundsEnabledChanged: (enabled: Boolean) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = stringResource(id = R.string.board_sounds))
            Spacer(modifier = Modifier.width(40.dp))
            Checkbox(
                checked = soundsEnabled,
                onCheckedChange = onSoundsEnabledChanged,
            )
        }
    }
}
