package dev.mcd.chess.ui.screen.choosemode

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.PersonSearch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@Composable
fun ChooseModeScreen(onPlayOnline: () -> Unit, onPlayBot: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "Choose Mode") })
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Card(modifier = Modifier
                    .weight(1f)
                    .clickable { onPlayOnline() }) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Play Online",
                            style = MaterialTheme.typography.subtitle1,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Icon(
                            modifier = Modifier.size(32.dp),
                            painter = rememberVectorPainter(image = Icons.Rounded.PersonSearch),
                            contentDescription = "Play Online"
                        )
                    }
                }
                Spacer(modifier = Modifier.width(24.dp))
                Card(modifier = Modifier
                    .weight(1f)
                    .clickable { onPlayBot() }) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Play Computer",
                            style = MaterialTheme.typography.subtitle1,
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Icon(
                            modifier = Modifier.size(32.dp),
                            painter = rememberVectorPainter(image = Icons.Rounded.Computer),
                            contentDescription = "Play Computer"
                        )
                    }
                }
            }
        }
    }
}
