package dev.mcd.chess

import dev.mcd.chess.api.domain.AuthResponse
import dev.mcd.chess.api.domain.GameSession
import dev.mcd.chess.api.domain.LobbyInfo
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.player.UserId

interface ChessApi {
    suspend fun generateId(): AuthResponse
    suspend fun findGame(authToken: String): GameSession
    suspend fun game(authToken: String, id: GameId): GameSession
    suspend fun gameForUser(authToken: String): List<GameSession>
    suspend fun joinGame(authToken: String, id: GameId, block: suspend OnlineGameChannel.() -> Unit)
    suspend fun lobbyInfo(excludeUser: UserId? = null): LobbyInfo
}
