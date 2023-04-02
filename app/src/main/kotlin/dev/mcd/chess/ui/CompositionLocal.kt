package dev.mcd.chess.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import dev.mcd.chess.ui.game.board.GameSessionManager
import dev.mcd.chess.ui.game.board.interaction.BoardInteraction
import dev.mcd.chess.ui.theme.ComposeAppColorScheme

val LocalGameSession = compositionLocalOf { GameSessionManager() }
val LocalBoardInteraction = compositionLocalOf { BoardInteraction() }
val LocalAppColors = compositionLocalOf<ComposeAppColorScheme> { ComposeAppColorScheme.Blue }

@Composable
fun rememberBoardColors(): ComposeAppColorScheme.BoardColors {
    val appColors = LocalAppColors.current
    return remember { appColors.boardColors }
}
