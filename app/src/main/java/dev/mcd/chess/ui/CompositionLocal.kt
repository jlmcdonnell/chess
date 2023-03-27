package dev.mcd.chess.ui

import androidx.compose.runtime.compositionLocalOf
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.common.player.HumanPlayer
import dev.mcd.chess.common.player.PlayerImage
import dev.mcd.chess.ui.game.board.GameSessionManager
import dev.mcd.chess.ui.game.board.interaction.BoardInteraction
import dev.mcd.chess.ui.theme.AppColors
import dev.mcd.chess.ui.theme.defaultBoardTheme

val LocalGameSession = compositionLocalOf { GameSessionManager() }
val LocalBoardTheme = compositionLocalOf { defaultBoardTheme }
val LocalAppColors = compositionLocalOf { AppColors() }
val LocalBoardInteraction = compositionLocalOf {
    BoardInteraction(
        GameSession(
            id = "",
            self = HumanPlayer("", PlayerImage.None, 0),
            selfSide = Side.WHITE,
            opponent = HumanPlayer("", PlayerImage.None, 0)
        )
    )
}
