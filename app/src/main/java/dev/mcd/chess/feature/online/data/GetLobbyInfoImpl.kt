package dev.mcd.chess.feature.online.data

import dev.mcd.chess.ChessApi
import dev.mcd.chess.api.domain.LobbyInfo
import dev.mcd.chess.feature.common.domain.AppPreferences
import dev.mcd.chess.feature.online.domain.GetLobbyInfo
import javax.inject.Inject

class GetLobbyInfoImpl @Inject constructor(
    private val chessApi: ChessApi,
    private val prefs: AppPreferences,
) : GetLobbyInfo {
    override suspend fun invoke(): LobbyInfo {
        return chessApi.lobbyInfo(excludeUser = prefs.userId())
    }
}
