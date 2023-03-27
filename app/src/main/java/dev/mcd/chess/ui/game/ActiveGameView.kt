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
import androidx.compose.runtime.ReusableContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.game.board.chessboard.ChessBoard
import dev.mcd.chess.ui.game.board.interaction.BoardInteraction
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun ActiveGameView(
    game: GameSession,
    onMove: (Move) -> Unit,
    onResign: () -> Unit,
    terminated: Boolean,
) {
    val sessionManager = LocalGameSession.current
    val boardInteraction = remember(game.id) { BoardInteraction(game) }

    LaunchedEffect(game) {
        Timber.d("Game ID: ${game.id}")
        sessionManager.updateSession(game)
        boardInteraction.moves().collectLatest {
            onMove(it)
        }
    }
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

    CompositionLocalProvider(LocalBoardInteraction provides boardInteraction) {
        ReusableContent(boardInteraction) {
            ChessBoard(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(ratio = 1f),
                gameId = game.id,
            )
        }
    }
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
        GameOptions(
            modifier = Modifier.align(Alignment.CenterEnd),
            terminated = terminated,
            onResignClicked = { onResign() },
            onUndoClicked = {
                game.undo()
            },
            onRedoClicked = {
                game.redo()
                boardInteraction.enableInteraction(game.isLive())
            },
        )
    }
}
