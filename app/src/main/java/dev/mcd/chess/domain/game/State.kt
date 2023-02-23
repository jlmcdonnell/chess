package dev.mcd.chess.domain.game

enum class State {
    STARTED,
    DRAW,
    WHITE_RESIGNED,
    BLACK_RESIGNED,
    WHITE_CHECKMATED,
    BLACK_CHECKMATED,
}
