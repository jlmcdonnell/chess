package dev.mcd.chess.feature.online.data

import dev.mcd.chess.ChessApi
import dev.mcd.chess.feature.common.domain.AppPreferences
import dev.mcd.chess.common.player.UserId
import dev.mcd.chess.feature.online.domain.GetOrCreateUser
import javax.inject.Inject

class GetOrCreateUserImpl @Inject constructor(
    private val api: ChessApi,
    private val prefs: AppPreferences,
) : GetOrCreateUser {

    override suspend fun invoke(): UserId {
        val userId = prefs.userId()
        val authToken = prefs.token()

        return if (userId != null && authToken != null) {
            userId
        } else {
            api.generateId().let { response ->
                prefs.storeUserId(response.userId)
                prefs.storeToken(response.token)
                response.userId
            }
        }
    }
}
