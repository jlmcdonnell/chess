package dev.mcd.chess.ui.game.board.chessboard

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import com.github.bhlangonijr.chesslib.Square
import dev.mcd.chess.R
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.extension.toDp
import dev.mcd.chess.ui.extension.topLeft
import dev.mcd.chess.ui.rememberBoardColors

context(BoardLayout)
@Composable
fun TargetHighlight() {
    val target by LocalBoardInteraction.current.targets().collectAsState(Square.NONE)
    val boardColors = rememberBoardColors()

    if (target != Square.NONE) {
        val offset by animateOffsetAsState(
            targetValue = target
                .topLeft()
                .minus(Offset(squareSize / 2, squareSize / 2)),
            animationSpec = spring(stiffness = Spring.StiffnessHigh),
            label = stringResource(R.string.target_highlight_desc),
        )
        Box(
            modifier = Modifier
                .offset(offset.x.toDp(), offset.y.toDp())
                .size(squareSizeDp * 2)
                .clip(CircleShape)
                .background(boardColors.targetSquareHighlight),
        )
    }
}
