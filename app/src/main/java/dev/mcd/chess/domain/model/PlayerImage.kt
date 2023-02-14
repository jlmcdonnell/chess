package dev.mcd.chess.domain.model

sealed interface PlayerImage {
    object None : PlayerImage
    class Local(val resId: Int) : PlayerImage
    class Url(val url: String) : PlayerImage
}
