package dev.mcd.chess.data

import dev.mcd.chess.ChessApi
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.data.api.ApiCredentialsStore
import dev.mcd.chess.domain.GetGameForUser
import javax.inject.Inject

class GetGameForUserImpl @Inject constructor(
    private val chessApi: ChessApi,
    private val credentialsStore: ApiCredentialsStore,
) : GetGameForUser {
    override suspend fun invoke(): GameId? {
        val token = credentialsStore.token() ?: return null
        return chessApi.gameForUser(token).firstOrNull()?.id
    }
}
