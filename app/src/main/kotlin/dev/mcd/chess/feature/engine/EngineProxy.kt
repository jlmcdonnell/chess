package dev.mcd.chess.feature.engine

interface EngineProxy {
    suspend fun start()
    suspend fun stop()
    suspend fun getMove(fen: String, depth: Int): String
}
