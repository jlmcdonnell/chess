package dev.mcd.chess.common.data

import dev.mcd.chess.ChessApi
import dev.mcd.chess.api.domain.GameSession
import dev.mcd.chess.common.domain.ApiCredentialsStore
import dev.mcd.chess.common.domain.FindGame
import javax.inject.Inject

class FindGameImpl @Inject constructor(
    private val credentialsStore: ApiCredentialsStore,
    private val chessApi: ChessApi,
) : FindGame {
    override suspend fun invoke(): GameSession {
        val token = credentialsStore.token() ?: throw Exception("No auth token")
        return chessApi.findGame(token)
    }
}
