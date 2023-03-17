package dev.mcd.chess.data

import dev.mcd.chess.ChessApi
import dev.mcd.chess.api.domain.GameSession
import dev.mcd.chess.data.api.ApiCredentialsStore
import dev.mcd.chess.domain.FindGame
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
