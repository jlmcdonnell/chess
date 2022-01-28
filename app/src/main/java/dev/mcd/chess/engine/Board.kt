package dev.mcd.chess.engine

class Board(
    private val positions: MutableList<Position> = mutableListOf()
) {
    fun validMoves(position: Position): List<Move> {
        return MovementRules.validMoves(position, positions)
    }
}
