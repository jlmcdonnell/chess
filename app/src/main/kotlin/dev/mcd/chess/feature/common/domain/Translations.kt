package dev.mcd.chess.feature.common.domain

interface Translations {
    val playerYou: String

    fun playerPuzzle(id: String): String
}
