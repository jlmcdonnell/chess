package dev.mcd.chess.online.domain

import dev.mcd.chess.common.player.UserId

interface AuthStore {
    suspend fun storeToken(token: String?)
    suspend fun storeUserId(userId: UserId?)
    suspend fun userId(): UserId?
    suspend fun token(): String?
}
