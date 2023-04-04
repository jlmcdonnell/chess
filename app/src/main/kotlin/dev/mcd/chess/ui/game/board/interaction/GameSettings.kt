package dev.mcd.chess.ui.game.board.interaction

data class GameSettings(
    val allowResign: Boolean = true,
    val showCapturedPieces: Boolean = true,
)
