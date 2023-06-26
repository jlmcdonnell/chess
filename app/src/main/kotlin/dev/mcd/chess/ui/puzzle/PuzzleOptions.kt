package dev.mcd.chess.ui.puzzle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.mcd.chess.R
import dev.mcd.chess.ui.theme.ChessTheme


@Composable
fun PuzzleOptionsDialog(
    modifier: Modifier = Modifier,
    ratingRange: IntRange,
    maxRatingRange: IntRange,
    onRatingRangeChanged: (IntRange) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
    ) {
        ElevatedCard(modifier = modifier) {
            Column(
                modifier = Modifier.padding(24.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = stringResource(id = R.string.rating_range))
                    Text(text = stringResource(id = R.string.rating_range_value, ratingRange.first, ratingRange.last))
                }
                Spacer(modifier = Modifier.height(16.dp))
                RangeSlider(
                    valueRange = maxRatingRange.toFloat(),
                    value = ratingRange.toFloat(),
                    onValueChange = {
                        onRatingRangeChanged(it.toInt())
                    },
                )
            }
        }
    }
}

private fun ClosedRange<Int>.toFloat() = start.toFloat()..endInclusive.toFloat()
private fun ClosedFloatingPointRange<Float>.toInt() = start.toInt()..endInclusive.toInt()

@Preview
@Composable
private fun PuzzleOptionsPreview() {
    ChessTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box {
                PuzzleOptionsDialog(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 64.dp, horizontal = 40.dp),
                    maxRatingRange = 350..3500,
                    ratingRange = 350..3500,
                    onRatingRangeChanged = { },
                    onDismissRequest = {},
                )
            }
        }
    }
}
