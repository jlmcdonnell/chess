package dev.mcd.chess.ui.puzzle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import dev.mcd.chess.R

@Composable
fun PuzzleCompleted(
    rating: Int,
    loading: Boolean = false,
    onNext: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
    ) {
        Column {
            Row(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Rounded.Checklist),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.width(24.dp))
                Text(
                    text = stringResource(id = R.string.correct),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Text(
                modifier = Modifier.padding(start = 64.dp),
                style = MaterialTheme.typography.bodySmall,
                text = buildAnnotatedString {
                    append(stringResource(R.string.puzzle_rating, rating))
                },
            )
            TextButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                onClick = onNext,
                enabled = !loading,
            ) {
                Text(stringResource(id = R.string.next))
            }
        }
    }
}
