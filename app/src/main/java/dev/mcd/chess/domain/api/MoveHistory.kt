package dev.mcd.chess.domain.api

data class MoveHistory(
    val fen: String,
    val moveList: List<String>,
)
