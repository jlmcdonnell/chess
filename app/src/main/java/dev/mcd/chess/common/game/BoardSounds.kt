package dev.mcd.chess.common.game

import dev.mcd.chess.common.game.local.ClientGameSession

interface BoardSounds {
    suspend fun notify()
    suspend fun awaitMoves(session: ClientGameSession)
}
