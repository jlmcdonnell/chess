package dev.mcd.chess.domain.prefs

interface AppPreferences {
    suspend fun host(): String
    suspend fun setHost(host: String)
}
