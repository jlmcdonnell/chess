package dev.mcd.chess.online.domain.usecase

import dev.mcd.chess.online.domain.entity.LobbyInfo

interface GetLobbyInfo {
    suspend operator fun invoke(): LobbyInfo
}
