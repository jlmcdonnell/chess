package dev.mcd.chess.online.data.usecase

import dev.mcd.chess.common.player.UserId
import dev.mcd.chess.online.domain.AuthStore
import dev.mcd.chess.online.domain.ChessApi
import dev.mcd.chess.online.domain.usecase.GetOrCreateUser
import javax.inject.Inject

internal class GetOrCreateUserImpl @Inject constructor(
    private val api: ChessApi,
    private val authStore: AuthStore,
) : GetOrCreateUser {

    override suspend fun invoke(): UserId {
        val userId = authStore.userId()
        val authToken = authStore.token()

        return if (userId != null && authToken != null) {
            userId
        } else {
            api.generateId().let { response ->
                authStore.storeUserId(response.userId)
                authStore.storeToken(response.token)
                response.userId
            }
        }
    }
}
