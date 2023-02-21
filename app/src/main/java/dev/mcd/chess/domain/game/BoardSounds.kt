package dev.mcd.chess.domain.game

interface BoardSounds {
    suspend fun notify()
    suspend fun awaitMoves(session: GameSession)
}