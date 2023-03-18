package dev.mcd.chess.feature.online.domain

import dev.mcd.chess.api.domain.LobbyInfo

interface GetLobbyInfo {
    suspend operator fun invoke(): LobbyInfo
}
