package dev.mcd.chess.ui.game

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ResignationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Resign") },
        text = { Text("Are you sure you want to resign?") },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("No")
            }
        },
    )
}
