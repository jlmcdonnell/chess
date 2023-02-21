package dev.mcd.chess.domain.player

sealed interface PlayerImage {
    object None : PlayerImage
    class Local(val resId: Int) : PlayerImage
    class Url(val url: String) : PlayerImage
}
