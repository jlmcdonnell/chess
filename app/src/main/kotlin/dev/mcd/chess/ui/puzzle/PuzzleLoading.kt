package dev.mcd.chess.ui.puzzle

import androidx.compose.animation.core.EaseInCirc
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutElastic
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.ui.extension.toPx
import dev.mcd.chess.ui.game.board.chessboard.Squares
import dev.mcd.chess.ui.game.board.chessboard.rememberBoardLayout

@Composable
fun Loading(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        rememberBoardLayout(
            boardWidth = 144.dp.toPx(),
            perspective = Side.WHITE,
        ).run {
            var alpha by remember { mutableStateOf(0.0f) }

            SideEffect {
                alpha = .5f
            }

            val alphaAnimation by animateFloatAsState(
                targetValue = alpha,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 2000, delayMillis = 200, easing = EaseInOutCubic),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "loading",
            )
            Column(horizontalAlignment = CenterHorizontally) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.size(32.dp))
                Squares(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .size(144.dp)
                        .alpha(
                            alpha = alphaAnimation,
                        ),
                    drawLabels = false,
                )
            }
        }
    }
}
