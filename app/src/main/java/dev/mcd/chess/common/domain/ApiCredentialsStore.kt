package dev.mcd.chess.common.domain

import dev.mcd.chess.common.player.UserId

interface ApiCredentialsStore {
    suspend fun storeToken(token: String)
    suspend fun storeUserId(userId: UserId)
    suspend fun userId(): UserId?
    suspend fun token(): String?
    suspend fun clear()
}
