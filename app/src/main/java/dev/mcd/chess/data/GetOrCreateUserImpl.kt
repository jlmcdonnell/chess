package dev.mcd.chess.data

import dev.mcd.chess.ChessApi
import dev.mcd.chess.common.player.UserId
import dev.mcd.chess.data.api.ApiCredentialsStore
import dev.mcd.chess.domain.GetOrCreateUser
import javax.inject.Inject

class GetOrCreateUserImpl @Inject constructor(
    private val api: ChessApi,
    private val credentialsStore: ApiCredentialsStore,
) : GetOrCreateUser {

    override suspend fun invoke(): UserId {
        val userId = credentialsStore.userId()
        val authToken = credentialsStore.token()

        return if (userId != null && authToken != null) {
            userId
        } else {
            api.generateId().let { response ->
                credentialsStore.storeUserId(response.userId)
                credentialsStore.storeToken(response.token)
                response.userId
            }
        }
    }
}
