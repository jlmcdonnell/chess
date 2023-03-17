package dev.mcd.chess.domain.prefs

interface AppPreferences {
    suspend fun host(): String
    suspend fun setHost(host: String)
    suspend fun setClickToMove(enabled: Boolean)
    suspend fun clickToMove(): Boolean
}
