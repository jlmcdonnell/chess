package dev.mcd.chess.common.domain

import dev.mcd.chess.api.domain.LobbyInfo
import dev.mcd.chess.common.game.GameId

interface GetLobbyInfo {
    suspend operator fun invoke(): LobbyInfo
}
