package dev.mcd.chess.feature.game.domain

interface BoardSounds {
    suspend fun notify()
    suspend fun awaitMoves(session: ClientGameSession)
}
