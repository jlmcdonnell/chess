package dev.mcd.chess.ui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.R
import dev.mcd.chess.common.game.TerminationReason
import dev.mcd.chess.ui.theme.ChessTheme

@Composable
fun GameTermination(
    reason: TerminationReason,
    onDismiss: () -> Unit,
    onRestart: (() -> Unit)?,
) {
    ElevatedCard(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
    ) {
        Column(
            Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
        ) {
            Reason(reason)
            Row(modifier = Modifier.align(Alignment.End)) {
                onRestart?.let {
                    TextButton(
                        onClick = {
                            onRestart()
                            onDismiss()
                        },
                    ) {
                        Text(stringResource(id = R.string.restart))
                    }
                }
                TextButton(onClick = { onDismiss() }) {
                    Text(stringResource(R.string.dismiss))
                }
            }
        }
    }
}

@Composable
private fun Reason(reason: TerminationReason) {
    val text = when {
        reason.draw -> stringResource(R.string.game_ended_by_draw)
        reason.sideMated == Side.WHITE -> stringResource(R.string.black_won_by_checkmate)
        reason.sideMated == Side.BLACK -> stringResource(R.string.white_won_by_checkmate)
        reason.resignation == Side.WHITE -> stringResource(R.string.white_resigned)
        reason.resignation == Side.BLACK -> stringResource(R.string.black_resigned)
        else -> throw Error("Unhandled reason: $reason")
    }
    Text(text = text)
}

@Composable
@Preview
private fun GameTerminationPreview() {
    ChessTheme {
        Box(modifier = Modifier.padding(24.dp)) {
            GameTermination(
                reason = TerminationReason(
                    draw = true,
                    sideMated = null,
                    resignation = null,
                ),
                onDismiss = {},
                onRestart = null,
            )
        }
    }
}
