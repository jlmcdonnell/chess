package dev.mcd.chess.ui.game

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.domain.GameSession
import dev.mcd.chess.ui.game.board.BoardInteraction
import dev.mcd.chess.ui.game.board.chessboard.ChessBoard
import dev.mcd.chess.ui.game.board.LocalBoardInteraction
import dev.mcd.chess.ui.game.board.LocalGameSession

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
        println("Game ID: ${game.id}")
        sessionManager.sessionUpdates.emit(game)
        boardInteraction.setPerspective(game.selfSide)
    }
    CompositionLocalProvider(
        LocalBoardInteraction provides boardInteraction
    ) {
        PlayerStrip(
            modifier = Modifier.padding(12.dp),
            player = game.opponent
        )
        CapturedPieces(side = game.selfSide)
        Spacer(Modifier.height(4.dp))
        ChessBoard(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 1f),
            onMove = { if (!terminated) onMove(it) },
        )
        Spacer(Modifier.height(4.dp))
        CapturedPieces(side = game.selfSide.flip())
        PlayerStrip(
            modifier = Modifier.padding(12.dp),
            player = game.self
        )
        if (!terminated) {
            Row {
                TextButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = onResign
                ) {
                    Text(text = "Resign")
                }
            }
        }
    }
}
