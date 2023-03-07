package dev.mcd.chess.domain.api

import dev.mcd.chess.domain.game.GameId
import dev.mcd.chess.domain.game.online.GameSession
import dev.mcd.chess.domain.game.online.OnlineGameChannel
import dev.mcd.chess.domain.player.UserId

interface ChessApi {
    suspend fun storeToken(token: String)
    suspend fun userId(): UserId?
    suspend fun generateId(): UserId
    suspend fun findGame(): GameSession
    suspend fun game(id: GameId): GameSession
    suspend fun gameForUser(): List<GameSession>
    suspend fun joinGame(id: GameId, block: suspend OnlineGameChannel.() -> Unit)
    suspend fun lobbyInfo(): LobbyInfo
    suspend fun clear()
}

