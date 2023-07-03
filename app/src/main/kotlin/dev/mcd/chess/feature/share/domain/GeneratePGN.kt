package dev.mcd.chess.feature.share.domain

import dev.mcd.chess.common.game.GameSession

interface GeneratePGN {
    operator fun invoke(session: GameSession): String
}
