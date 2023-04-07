package dev.mcd.chess.ui.game.board

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.R
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.extension.drawableResource
import dev.mcd.chess.ui.game.board.interaction.BoardInteraction
import dev.mcd.chess.ui.theme.ChessTheme

@Composable
fun PromotionSelector(modifier: Modifier) {
    val boardInteraction = LocalBoardInteraction.current
    val promotionMoves by boardInteraction.displayPromotions().collectAsState(emptyList())

    BackHandler {
        boardInteraction.cancelPromotion()
    }
    if (promotionMoves.isNotEmpty()) {
        Box(
            modifier
                .testTag("promotion-selector")
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                ) {
                    boardInteraction.cancelPromotion()
                },
        ) {
            ElevatedCard(modifier = Modifier.align(Alignment.Center)) {
                Column(
                    modifier = Modifier.background(Color.DarkGray),
                ) {
                    promotionMoves.forEachIndexed { i, move ->
                        val verticalPadding = if (i == 0 || i == promotionMoves.lastIndex) 8.dp else 4.dp
                        Image(
                            modifier = Modifier
                                .semantics {
                                    role = Role.Button
                                    contentDescription = move.toString()
                                }
                                .clickable { boardInteraction.promote(move) }
                                .padding(vertical = verticalPadding, horizontal = 8.dp)
                                .size(64.dp),
                            painter = painterResource(id = move.promotion.drawableResource()),
                            contentDescription = move.promotion.sanSymbol,
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun PromotionSelectorPreview() {
    ChessTheme {
        Surface {
            val boardInteraction = BoardInteraction(GameSession())
            val promotionMoves = listOf(
                Move("e7e8q", Side.WHITE),
                Move("e7e8r", Side.WHITE),
                Move("e7e8b", Side.WHITE),
                Move("e7e8n", Side.WHITE),
            )

            LaunchedEffect(Unit) {
                boardInteraction.selectPromotion(promotionMoves)
            }

            CompositionLocalProvider(LocalBoardInteraction provides boardInteraction) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    PromotionSelector(
                        Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                    )
                }
            }
        }
    }
}
