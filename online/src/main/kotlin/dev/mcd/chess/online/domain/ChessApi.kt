package dev.mcd.chess.online.domain

import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.player.UserId
import dev.mcd.chess.online.domain.entity.AuthResponse
import dev.mcd.chess.online.domain.entity.GameSession
import dev.mcd.chess.online.domain.entity.LobbyInfo
import dev.mcd.chess.online.domain.OnlineGameChannel

interface ChessApi {
    suspend fun generateId(): AuthResponse
    suspend fun findGame(authToken: String): GameSession
    suspend fun game(authToken: String, id: GameId): GameSession
    suspend fun gameForUser(authToken: String): List<GameSession>
    suspend fun joinGame(authToken: String, id: GameId, block: suspend OnlineGameChannel.() -> Unit)
    suspend fun lobbyInfo(excludeUser: UserId? = null): LobbyInfo
}
