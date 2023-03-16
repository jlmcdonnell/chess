package dev.mcd.chess.engine.domain

interface ChessEngine {
    suspend fun startAndWait()
    suspend fun getMove(fen: String, level: Int, depth: Int): String
}
