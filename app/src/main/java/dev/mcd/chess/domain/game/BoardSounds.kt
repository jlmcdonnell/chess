package dev.mcd.chess.domain.game

import dev.mcd.chess.domain.game.local.ClientGameSession

interface BoardSounds {
    suspend fun notify()
    suspend fun awaitMoves(session: ClientGameSession)
}
