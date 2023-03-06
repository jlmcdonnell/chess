package dev.mcd.chess.domain.player

sealed interface PlayerImage {
    object None : PlayerImage
    class Url(val url: String) : PlayerImage
}
