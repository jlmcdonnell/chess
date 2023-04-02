package dev.mcd.chess.feature.common.domain

import dev.mcd.chess.common.player.UserId
import dev.mcd.chess.online.domain.AuthStore

interface AppPreferences : AuthStore {
    suspend fun host(): String
    suspend fun setHost(host: String)
    suspend fun setClickToMove(enabled: Boolean)
    suspend fun clickToMove(): Boolean
    suspend fun clear()
    override suspend fun storeToken(token: String?)
    override suspend fun storeUserId(userId: UserId?)
    override suspend fun userId(): UserId?
    override suspend fun token(): String?
}
