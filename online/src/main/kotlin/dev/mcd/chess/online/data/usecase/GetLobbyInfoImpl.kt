package dev.mcd.chess.online.data.usecase

import dev.mcd.chess.online.domain.AuthStore
import dev.mcd.chess.online.domain.ChessApi
import dev.mcd.chess.online.domain.entity.LobbyInfo
import dev.mcd.chess.online.domain.usecase.GetLobbyInfo
import javax.inject.Inject

internal class GetLobbyInfoImpl @Inject constructor(
    private val chessApi: ChessApi,
    private val authStore: AuthStore,
) : GetLobbyInfo {
    override suspend fun invoke(): LobbyInfo {
        return chessApi.lobbyInfo(excludeUser = authStore.userId())
    }
}
