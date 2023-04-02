package dev.mcd.chess.common.player

sealed interface PlayerImage {
    object Default : PlayerImage
    object Bot : PlayerImage
    class Url(val url: String) : PlayerImage
}
