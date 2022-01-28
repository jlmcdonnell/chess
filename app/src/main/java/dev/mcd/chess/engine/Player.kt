package dev.mcd.chess.engine

enum class Player {
    White, Black;

    val white get() = this == White
}