package dev.mcd.chess

import dev.mcd.chess.api.data.AuthResponse
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.game.online.GameSession
import dev.mcd.chess.common.player.UserId
import dev.mcd.chess.api.domain.LobbyInfo

interface ChessApi {
    suspend fun generateId(): AuthResponse
    suspend fun findGame(authToken: String): GameSession
    suspend fun game(authToken: String, id: GameId): GameSession
    suspend fun gameForUser(authToken: String): List<GameSession>
    suspend fun joinGame(authToken: String, id: GameId, block: suspend OnlineGameChannel.() -> Unit)
    suspend fun lobbyInfo(): LobbyInfo
}
