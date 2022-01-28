package dev.mcd.chess.engine

import org.junit.Assert.assertTrue
import org.junit.Test

class CoordinateTest {

    val a1 = Coordinate(0, 0)
    val a2 = Coordinate(0, 1)
    val a8 = Coordinate(0, 7)
    val b1 = Coordinate(1, 0)
    val b3 = Coordinate(1, 2)
    val c3 = Coordinate(2, 2)
    val h1 = Coordinate(7, 0)

    @Test
    fun diagonalTest() {
        assertTrue(a1 diagonalTo c3)
        assertTrue(c3 diagonalTo a1)
        assertTrue(a8 diagonalTo h1)
        assertTrue(h1 diagonalTo a8)
    }

    @Test
    fun lineTest() {
        assertTrue(a1 knightsTo b3)
        assertTrue(b3 knightsTo a1)
    }

    @Test

    fun adjacentTest() {
        assertTrue(a1 adjacentTo a2)
        assertTrue(a1 adjacentTo b1)
        assertTrue(a2 adjacentTo b1)
    }
}