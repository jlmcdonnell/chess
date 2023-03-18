package dev.mcd.chess.online.data.usecase

import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.online.domain.AuthStore
import dev.mcd.chess.online.domain.ChessApi
import dev.mcd.chess.online.domain.usecase.GetGameForUser
import javax.inject.Inject

internal class GetGameForUserImpl @Inject constructor(
    private val chessApi: ChessApi,
    private val authStore: AuthStore,
) : GetGameForUser {
    override suspend fun invoke(): GameId? {
        val token = authStore.token() ?: return null
        return chessApi.gameForUser(token).firstOrNull()?.id
    }
}
