package dev.mcd.chess.feature.common.domain

interface Translations {
    val pgnSiteName: String
    val playerYou: String
    val pgnBotName: String
    val pgnOnlineGame: String
    val pgnAnalysis: String

    fun playerPuzzle(id: String): String
}
