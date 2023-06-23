package dev.mcd.chess.ui.settings

import androidx.compose.foundation.layout.height
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mcd.chess.feature.common.domain.AppColorScheme

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
