package dev.mcd.chess.feature.game.domain

import dev.mcd.chess.common.game.ClientGameSession

interface BoardSounds {
    suspend fun notify()
    suspend fun awaitMoves(session: ClientGameSession)
}
