package dev.mcd.chess.feature.online.data

import dev.mcd.chess.ChessApi
import dev.mcd.chess.api.domain.GameSession
import dev.mcd.chess.feature.common.domain.AppPreferences
import dev.mcd.chess.feature.online.domain.FindGame
import javax.inject.Inject

class FindGameImpl @Inject constructor(
    private val prefs: AppPreferences,
    private val chessApi: ChessApi,
) : FindGame {
    override suspend fun invoke(): GameSession {
        val token = prefs.token() ?: throw Exception("No auth token")
        return chessApi.findGame(token)
    }
}
