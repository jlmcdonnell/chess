package dev.mcd.chess.common.player

sealed interface PlayerImage {
    object None : PlayerImage
    class Url(val url: String) : PlayerImage
}
