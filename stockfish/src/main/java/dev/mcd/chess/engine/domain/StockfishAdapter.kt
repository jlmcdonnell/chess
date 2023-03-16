package dev.mcd.chess.engine.domain

interface StockfishAdapter {
    suspend fun startAndWait()
    suspend fun getMove(fen: String, level: Int, depth: Int): String
}
