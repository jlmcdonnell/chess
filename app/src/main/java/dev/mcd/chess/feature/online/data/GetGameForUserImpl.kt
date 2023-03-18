package dev.mcd.chess.feature.online.data

import dev.mcd.chess.ChessApi
import dev.mcd.chess.feature.common.domain.AppPreferences
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.feature.online.domain.GetGameForUser
import javax.inject.Inject

class GetGameForUserImpl @Inject constructor(
    private val chessApi: ChessApi,
    private val prefs: AppPreferences,
) : GetGameForUser {
    override suspend fun invoke(): GameId? {
        val token = prefs.token() ?: return null
        return chessApi.gameForUser(token).firstOrNull()?.id
    }
}
