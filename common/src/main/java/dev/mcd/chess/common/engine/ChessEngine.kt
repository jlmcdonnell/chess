package dev.mcd.chess.common.engine

interface ChessEngine {
    suspend fun startAndWait()
    suspend fun getMove(fen: String, level: Int, depth: Int): String
}
