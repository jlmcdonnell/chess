package dev.mcd.chess.data

import dev.mcd.chess.ChessApi
import dev.mcd.chess.api.domain.LobbyInfo
import dev.mcd.chess.data.api.ApiCredentialsStore
import dev.mcd.chess.domain.GetLobbyInfo
import javax.inject.Inject

class GetLobbyInfoImpl @Inject constructor(
    private val chessApi: ChessApi,
    private val credentialsStore: ApiCredentialsStore,
) : GetLobbyInfo {
    override suspend fun invoke(): LobbyInfo {
        return chessApi.lobbyInfo(excludeUser = credentialsStore.userId())
    }
}
