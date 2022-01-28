package dev.mcd.chess.engine

class Game {
    val board = Board()
    val moves = mutableListOf<Move>()

    fun currentTurn(): Player {
        return if (moves.isEmpty() || moves.last().player == Player.Black) {
            Player.White
        } else {
            Player.Black
        }
    }
}