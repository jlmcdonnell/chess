package dev.mcd.chess.engine

import kotlin.math.abs

data class Position(
    val player: Player,
    val piece: Piece,
    val coordinate: Coordinate,
)

data class Coordinate(
    val x: Int,
    val y: Int,
) {

    infix fun diagonalTo(other: Coordinate): Boolean {
        val (x2, y2) = other
        return abs((y2 - y) / (x2 - x)) == 1
    }

    infix fun abeamTo(other: Coordinate): Boolean {
        return x == other.x || y == other.y
    }

    infix fun knightsTo(other: Coordinate): Boolean {
        val (x2, y2) = other
        val dX = abs(x - x2)
        val dY = abs(y - y2)
        return when {
            dX == 2 -> dY == 1
            dY == 2 -> dX == 1
            else -> false
        }
    }

    infix fun adjacentTo(other: Coordinate): Boolean {
        if (this == other) return false

        val (x2, y2) = other
        if (abs(x - x2) > 1 || abs(y - y2) > 1) {
            return false
        }
        return true
    }

    val diagonals get() = allCoordinates.filter { this diagonalTo it }
    val lines get() = allCoordinates.filter { this abeamTo it }
    val knights get() = allCoordinates.filter { this knightsTo it }
    val adjacent get() = allCoordinates.filter { this adjacentTo it }

    val displayName = "${xSquares[x]}${ySquares[y]}"

    infix fun moveY(dY: Int): Coordinate = copy(y = y + dY)
    infix fun moveX(dX: Int): Coordinate = copy(x = x + dX)

    companion object {
        private val xSquares = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i")
        private val ySquares = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    }
}

val allCoordinates = 0.until(8)
    .map { x ->
        0.until(8).map { y ->
            Coordinate(x, y)
        }
    }.flatten()
