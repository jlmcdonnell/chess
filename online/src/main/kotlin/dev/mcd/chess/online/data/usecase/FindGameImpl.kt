package dev.mcd.chess.online.data.usecase

import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.online.domain.AuthStore
import dev.mcd.chess.online.domain.ChessApi
import dev.mcd.chess.online.domain.usecase.FindGame
import javax.inject.Inject

internal class FindGameImpl @Inject constructor(
    private val authStore: AuthStore,
    private val chessApi: ChessApi,
) : FindGame {
    override suspend fun invoke(): GameId {
        val token = authStore.token() ?: throw Exception("No auth token")
        return chessApi.findGame(token)
    }
}
