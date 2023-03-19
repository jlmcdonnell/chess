package dev.mcd.chess.ui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.game.board.chessboard.ChessBoard
import dev.mcd.chess.ui.game.board.interaction.BoardInteraction
import timber.log.Timber

@Composable
fun ActiveGameView(
    game: GameSession,
    onMove: (Move) -> Unit,
    onResign: () -> Unit,
    terminated: Boolean,
) {
    val boardInteraction by remember { mutableStateOf(BoardInteraction()) }
    val sessionManager = LocalGameSession.current

    LaunchedEffect(game) {
        Timber.d("Game ID: ${game.id}")
        sessionManager.updateSession(game)
        boardInteraction.setPerspective(game.selfSide)
    }
    CompositionLocalProvider(
        LocalBoardInteraction provides boardInteraction
    ) {
        PlayerStrip(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(top = 16.dp, bottom = 8.dp),
            player = game.opponent
        )
        CapturedPieces(
            modifier = Modifier.padding(horizontal = 12.dp),
            side = game.selfSide,
        )
        Spacer(Modifier.height(4.dp))
        ChessBoard(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 1f),
            onMove = { onMove(it) },
        )
        Spacer(Modifier.height(4.dp))
        CapturedPieces(
            modifier = Modifier.padding(horizontal = 12.dp),
            side = game.selfSide.flip()
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterStart,
        ) {
            PlayerStrip(
                modifier = Modifier,
                player = game.self
            )
            if (!terminated) {
                GameOptions(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onResignClicked = { onResign() }
                )
            }
        }
    }
}
