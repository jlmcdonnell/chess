package dev.mcd.chess.game.domain

interface BoardSounds {
    suspend fun notify()
    suspend fun awaitMoves(session: ClientGameSession)
}
