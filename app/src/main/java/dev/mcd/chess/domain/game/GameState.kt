package dev.mcd.chess.domain.game

enum class GameState {
    STARTED,
    DRAW,
    WHITE_RESIGNED,
    BLACK_RESIGNED,
    WHITE_CHECKMATED,
    BLACK_CHECKMATED,
}
