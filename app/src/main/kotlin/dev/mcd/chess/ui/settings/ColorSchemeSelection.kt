package dev.mcd.chess.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.R
import dev.mcd.chess.feature.common.domain.AppColorScheme
import dev.mcd.chess.ui.extension.toPx
import dev.mcd.chess.ui.game.board.chessboard.Squares
import dev.mcd.chess.ui.game.board.chessboard.rememberBoardLayout

@Composable
fun ColorSchemeSelection(
    colorScheme: AppColorScheme,
    onColorSchemeChanged: (AppColorScheme) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(24.dp),
            text = stringResource(R.string.set_color_scheme),
        )
        LazyRow(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Absolute.spacedBy(12.dp),
        ) {
            items(AppColorScheme.values()) { scheme ->
                ColorSchemeChip(
                    colorScheme = scheme,
                    isSelected = scheme == colorScheme,
                    onClick = { onColorSchemeChanged(scheme) },
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .size(200.dp)
                .clip(MaterialTheme.shapes.extraLarge),
        ) {
            rememberBoardLayout(boardWidth = 200.dp.toPx(), Side.WHITE).run {
                Squares(drawLabels = false)
            }
        }
    }
}
