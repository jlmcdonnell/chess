package dev.mcd.chess.feature.game.domain

import dev.mcd.chess.common.game.GameSession

interface BoardSounds {
    suspend fun notify()
    suspend fun awaitMoves(session: GameSession)
}
