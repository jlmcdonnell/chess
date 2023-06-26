package dev.mcd.chess.feature.common.domain

import dev.mcd.chess.common.player.UserId
import dev.mcd.chess.online.domain.AuthStore
import kotlinx.coroutines.flow.Flow

interface AppPreferences : AuthStore {
    suspend fun host(): String
    suspend fun setHost(host: String)
    suspend fun setClickToMove(enabled: Boolean)
    suspend fun clickToMove(): Boolean
    suspend fun colorSchemeUpdates(): Flow<String?>
    suspend fun colorScheme(): String?
    suspend fun setColorScheme(colorScheme: String)
    suspend fun setSoundsEnabled(enabled: Boolean)
    suspend fun soundsEnabled(): Boolean
    suspend fun puzzleRatingRange(): IntRange
    suspend fun setPuzzleRatingRange(range: IntRange)
    suspend fun clear()

    override suspend fun storeToken(token: String?)
    override suspend fun storeUserId(userId: UserId?)
    override suspend fun userId(): UserId?
    override suspend fun token(): String?
}
