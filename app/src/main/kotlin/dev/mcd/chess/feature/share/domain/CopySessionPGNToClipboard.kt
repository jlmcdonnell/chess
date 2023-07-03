package dev.mcd.chess.feature.share.domain

import dev.mcd.chess.common.game.GameSession

interface CopySessionPGNToClipboard {
    suspend operator fun invoke(session: GameSession)
}
