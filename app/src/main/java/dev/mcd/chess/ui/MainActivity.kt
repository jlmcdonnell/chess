package dev.mcd.chess.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dev.mcd.chess.ui.Routing
import dev.mcd.chess.ui.theme.ChessTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChessTheme {
                val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
                Surface(
                    modifier = Modifier
                        .padding(systemBarsPadding)
                        .fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Routing()
                }
            }
        }
    }
}

