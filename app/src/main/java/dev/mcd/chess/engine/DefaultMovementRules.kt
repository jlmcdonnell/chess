package dev.mcd.chess.engine

import dev.mcd.chess.engine.Piece.*

object MovementRules {

    fun validMoves(position: Position, board: List<Position>): List<Move> {
        return logicalMovesForPosition(position = position).map { newCoordinate ->
            Move(
                from = position,
                to = position.copy(
                    coordinate = newCoordinate
                )
            )
        }
    }

    private fun logicalMovesForPosition(position: Position): List<Coordinate> {
        with(position) {
            return when (piece) {
                Bishop -> coordinate.diagonals
                King -> coordinate.adjacent
                Knight -> coordinate.knights
                Pawn -> {
                    if (player.white) {
                        listOf(coordinate moveY 1)
                    } else {
                        listOf(coordinate moveY -1)
                    }
                }
                Queen -> coordinate.diagonals + coordinate.lines
                Rook -> coordinate.lines
            }
        }
    }
}
