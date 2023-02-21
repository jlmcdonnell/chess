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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.domain.bot.Bot
import dev.mcd.chess.domain.bot.bots
import dev.mcd.chess.ui.extension.drawableResource
import dev.mcd.chess.ui.player.PlayerImageView

@Composable
fun SelectBotScreen(onBotSelected: (slug: String, side: String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Play Computer") })
        }
    ) { padding ->
        var selectedSide by remember { mutableStateOf(Side.WHITE) }

        Column(Modifier.padding(padding), horizontalAlignment = CenterHorizontally) {
            Card(modifier = Modifier.padding(16.dp)) {
                Row {
                    Side.values().forEach { side ->
                        val piece = if (side == Side.WHITE) Piece.WHITE_KING else Piece.BLACK_KING
                        Image(
                            modifier = Modifier
                                .background(Color(0xFF302D2D))
                                .clickable { selectedSide = side }
                                .let {
                                    if (selectedSide == side) {
                                        it.border(
                                            width = 4.dp,
                                            color = MaterialTheme.colors.primary,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    } else it
                                }
                                .size(64.dp)
                                .padding(8.dp),
                            painter = painterResource(piece.drawableResource()),
                            contentDescription = side.name
                        )
                    }
                }
            }

            LazyColumn {
                items(bots, { it.slug }) { bot ->
                    BotItem(bot = bot) {
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
    bot: Bot,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlayerImageView(image = bot.image)
            Spacer(modifier = Modifier.width(24.dp))
            Text(text = bot.name)
        }
    }
}
