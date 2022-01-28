package dev.mcd.chess.engine

data class Move(
    val from: Position,
    val to: Position,
) {
    val player get() = from.player
}