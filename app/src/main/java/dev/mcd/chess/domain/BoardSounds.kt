package dev.mcd.chess.domain

interface BoardSounds {
    suspend fun notify()
    suspend fun awaitMoves(session: GameSession)
}