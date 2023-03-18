package dev.mcd.chess.feature.common.domain

import dev.mcd.chess.common.player.UserId

interface AppPreferences {
    suspend fun host(): String
    suspend fun setHost(host: String)
    suspend fun setClickToMove(enabled: Boolean)
    suspend fun clickToMove(): Boolean
    suspend fun storeToken(token: String?)
    suspend fun storeUserId(userId: UserId?)
    suspend fun userId(): UserId?
    suspend fun token(): String?
    suspend fun clear()
}
