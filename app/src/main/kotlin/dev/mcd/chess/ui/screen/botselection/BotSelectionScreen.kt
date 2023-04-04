package dev.mcd.chess.ui.screen.botselection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.R
import dev.mcd.chess.feature.game.domain.DefaultBots
import dev.mcd.chess.ui.extension.drawableResource
import dev.mcd.chess.ui.player.BotImageView

@Composable
fun BotSelectionScreen(
    onBotSelected: (slug: String, side: String) -> Unit,
    onDismiss: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.play_computer)) },
                navigationIcon = {
                    IconButton(onClick = { onDismiss() }) {
                        Icon(painter = rememberVectorPainter(image = Icons.Rounded.ArrowBack), contentDescription = stringResource(R.string.back))
                    }
                },
            )
        },
    ) { padding ->
        var selectedSide by remember { mutableStateOf(Side.WHITE) }

        Column(Modifier.padding(padding), horizontalAlignment = CenterHorizontally) {
            Row {
                Side.values().forEach { side ->
                    val piece = if (side == Side.WHITE) Piece.WHITE_KING else Piece.BLACK_KING
                    Image(
                        modifier = Modifier
                            .padding(vertical = 24.dp, horizontal = 16.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedSide = side }
                            .let {
                                if (selectedSide == side) {
                                    it.border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        shape = CircleShape,
                                    )
                                } else {
                                    it
                                }
                            }
                            .size(64.dp)
                            .padding(8.dp),
                        painter = painterResource(piece.drawableResource()),
                        contentDescription = side.name,
                    )
                }
            }

            LazyColumn {
                items(DefaultBots.bots(), { it.slug }) { bot ->
                    BotItem(botName = bot.name) {
                        onBotSelected(bot.slug, selectedSide.name)
                    }
                }
            }
        }
    }
}

@Composable
private fun BotItem(
    modifier: Modifier = Modifier,
    botName: String,
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BotImageView()
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = botName,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
