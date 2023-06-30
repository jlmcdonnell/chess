package dev.mcd.chess.ui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReusableContent
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.compose.StableHolder
import dev.mcd.chess.ui.game.board.chessboard.ChessBoard
import dev.mcd.chess.ui.game.board.interaction.BoardInteraction
import dev.mcd.chess.ui.game.board.interaction.GameSettings
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun GameView(
    gameHolder: StableHolder<GameSession>,
    onMove: (Move) -> Unit,
    onResign: () -> Unit = {},
    boardWidth: @Composable () -> Float = { LocalView.current.width.toFloat() },
    settings: GameSettings = GameSettings(),
) {
    val (game) = gameHolder
    val sessionManager = LocalGameSession.current
    val boardInteraction = remember(game.id) { BoardInteraction(game) }

    LaunchedEffect(game) {
        Timber.d("Game ID: ${game.id}")
        sessionManager.updateSession(game)
        boardInteraction.moves().collectLatest {
            onMove(it)
        }
    }

    ReusableContent(key = game.id) {
        CompositionLocalProvider(LocalBoardInteraction provides boardInteraction) {
            PlayerStrip(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(top = 16.dp, bottom = 8.dp),
                player = game.opponent,
            )
            if (settings.showCapturedPieces) {
                CapturedPieces(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    side = game.selfSide,
                )
            }
            ChessBoard(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
                    .aspectRatio(ratio = 1f),
                boardWidth = boardWidth(),
            )
            if (settings.showCapturedPieces) {
                CapturedPieces(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    side = game.selfSide.flip(),
                )
            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart,
            ) {
                PlayerStrip(
                    player = game.self,
                )
                GameOptions(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onResignClicked = onResign,
                    allowResign = settings.allowResign,
                )
            }
        }
    }
}
