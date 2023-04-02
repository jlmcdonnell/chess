package dev.mcd.chess.ui

import androidx.compose.runtime.compositionLocalOf
import dev.mcd.chess.ui.game.board.GameSessionManager
import dev.mcd.chess.ui.game.board.interaction.BoardInteraction

val LocalGameSession = compositionLocalOf { GameSessionManager() }
val LocalBoardInteraction = compositionLocalOf { BoardInteraction() }
