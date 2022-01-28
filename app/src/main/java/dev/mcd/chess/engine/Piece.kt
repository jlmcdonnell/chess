package dev.mcd.chess.engine

sealed class Piece(
    val name: String,
    val value: Int,
) {
    object Pawn : Piece(
        name = "Pawn",
        value = 1,
    )

    object Rook : Piece(
        name = "Rook",
        value = 5,
    )

    object Knight : Piece(
        name = "Knight",
        value = 3,
    )

    object Bishop : Piece(
        name = "Bishop",
        value = 3,
    )

    object Queen : Piece(
        name = "Queen",
        value = 9,
    )

    object King : Piece(
        name = "King",
        value = 0,
    )
}