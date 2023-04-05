package dev.mcd.chess.common.engine

interface ChessEngine {
    fun init()
    suspend fun awaitReady()
    suspend fun startAndWait()
    suspend fun getMove(fen: String, depth: Int): String
}
