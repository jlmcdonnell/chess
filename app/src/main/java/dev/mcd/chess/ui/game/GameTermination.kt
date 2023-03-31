package dev.mcd.chess.ui.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.common.game.TerminationReason

@Composable
fun GameTermination(
    reason: TerminationReason,
    onDismiss: () -> Unit,
    onRestart: (() -> Unit)?,
) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
    ) {
        Column(
            Modifier
                .padding(12.dp)
                .fillMaxWidth(),
        ) {
            Reason(reason)
            Row(modifier = Modifier.align(Alignment.End)) {
                onRestart?.let {
                    TextButton(onClick = {
                        onRestart()
                        onDismiss()
                    }) {
                        Text("Restart")
                    }
                }
                TextButton(onClick = { onDismiss() }) {
                    Text("Dismiss")
                }
            }
        }
    }
}

@Composable
private fun Reason(reason: TerminationReason) {
    val text = when {
        reason.sideMated == Side.WHITE -> {
            "Black won by checkmate"
        }
        reason.sideMated == Side.BLACK -> {
            "White won by checkmate"
        }
        reason.draw -> {
            "Game ended by draw"
        }
        reason.resignation == Side.WHITE -> {
            "White resigned"
        }
        reason.resignation == Side.BLACK -> {
            "Black resigned"
        }
        else -> throw Error("Unhandled reason: $reason")
    }
    Text(text = text)
}
