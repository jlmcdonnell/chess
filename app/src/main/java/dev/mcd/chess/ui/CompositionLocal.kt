package dev.mcd.chess.ui

import androidx.compose.runtime.compositionLocalOf
import dev.mcd.chess.ui.game.board.interaction.BoardInteraction
import dev.mcd.chess.ui.game.board.GameSessionManager
import dev.mcd.chess.ui.theme.AppColors
import dev.mcd.chess.ui.theme.defaultBoardTheme

val LocalGameSession = compositionLocalOf { GameSessionManager() }
val LocalBoardInteraction = compositionLocalOf { BoardInteraction() }
val LocalBoardTheme = compositionLocalOf { defaultBoardTheme }
val LocalAppColors = compositionLocalOf { AppColors() }
