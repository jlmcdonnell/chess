package dev.mcd.chess.feature.game.domain.usecase

import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.common.player.Bot

interface StartBotGame {
    suspend operator fun invoke(side: Side, bot: Bot)
}
