package dev.mcd.chess.ui.puzzle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.NavigateNext
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.mcd.chess.R
import dev.mcd.chess.ui.theme.ChessTheme

@Composable
fun PuzzleCompleted(
    modifier: Modifier = Modifier,
    rating: Int,
    loading: Boolean = false,
    onNext: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Rounded.Checklist),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(id = R.string.correct),
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        modifier = Modifier,
                        style = MaterialTheme.typography.bodySmall,
                        text = buildAnnotatedString {
                            append(stringResource(R.string.puzzle_rating, rating))
                        },
                    )
                }
            }
            Spacer(modifier = Modifier.width(32.dp))
            FloatingActionButton(
                modifier = Modifier,
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = {
                    if (!loading) {
                        onNext()
                    }
                },
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = rememberVectorPainter(image = Icons.Rounded.NavigateNext),
                        contentDescription = stringResource(id = R.string.next),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun PuzzleCompletedPreview() {
    ChessTheme {
        Surface(Modifier.width(400.dp)) {
            PuzzleCompleted(
                Modifier.padding(24.dp),
                rating = 400,
                onNext = {},
            )
        }
    }
}
