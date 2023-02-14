package dev.mcd.chess.ui.game

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.domain.model.TerminationReason

@Composable
fun GameTerminationDialog(
    reason: TerminationReason,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Text(text = "OK")
        },
        title = {
            val title = when {
                reason.sideMated == Side.WHITE -> {
                    "Black won by checkmate"
                }
                reason.sideMated == Side.BLACK -> {
                    "White won by checkmate"
                }
                reason.draw -> {
                    "Call it a draw!"
                }
                reason.resignation == Side.WHITE -> {
                    "White resigned"
                }
                reason.resignation == Side.BLACK -> {
                    "Black resigned"
                }
                else -> throw Error("Unhandled reason: $reason")
            }
            Text(text = title)
        }
    )
}
