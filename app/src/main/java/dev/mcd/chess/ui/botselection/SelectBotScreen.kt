package dev.mcd.chess.ui.botselection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mcd.chess.domain.model.Bot
import dev.mcd.chess.domain.model.bots
import dev.mcd.chess.ui.player.PlayerImageView

@Composable
fun SelectBotScreen(onBotSelected: (String) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Select Opponent") })
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            LazyColumn {
                items(bots, { it.slug }) { bot ->
                    BotItem(bot = bot) {
                        onBotSelected(bot.slug)
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
