package dev.mcd.chess.ui.game.board

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.mcd.chess.ui.extension.drawableResource

@Composable
fun PromotionSelector(modifier: Modifier) {
    val boardInteraction = LocalBoardInteraction.current
    val promotionMoves by boardInteraction.selectPromotion().collectAsState(emptyList())
    if (promotionMoves.isNotEmpty()) {
        Card(modifier = modifier) {
            Column(
                modifier = Modifier.background(Color.DarkGray)
            ) {
                promotionMoves.forEachIndexed { i, move ->
                    val verticalPadding = if (i == 0 || i == promotionMoves.lastIndex) 8.dp else 4.dp
                    Image(
                        modifier = Modifier
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
