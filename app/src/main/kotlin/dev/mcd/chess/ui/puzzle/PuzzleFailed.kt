package dev.mcd.chess.ui.puzzle

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
import androidx.compose.ui.unit.dp
import dev.mcd.chess.R

@Composable
fun PuzzleFailed(
    onSkip: () -> Unit,
    onRetry: () -> Unit,
    loading: Boolean,
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
            Text(text = stringResource(id = R.string.incorrect))
            Row(
                modifier = Modifier.align(Alignment.End),
            ) {
                TextButton(
                    enabled = !loading,
                    onClick = onSkip,
                ) {
                    Text(stringResource(id = R.string.skip))
                }
                TextButton(
                    onClick = onRetry,
                ) {
                    Text(stringResource(id = R.string.retry))
                }
            }
        }
    }
}
