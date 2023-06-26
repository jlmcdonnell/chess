package dev.mcd.chess.online.domain

import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.player.UserId
import dev.mcd.chess.online.domain.entity.AuthResponse
import dev.mcd.chess.online.domain.entity.LobbyInfo
import dev.mcd.chess.online.domain.entity.Puzzle

internal interface ChessApi {
    suspend fun generateId(): AuthResponse
    suspend fun gameForUser(authToken: String): List<GameId>
    suspend fun findGame(authToken: String): GameId
    suspend fun joinGame(authToken: String, id: GameId, block: suspend OnlineGameChannel.() -> Unit)
    suspend fun lobbyInfo(excludeUser: UserId? = null): LobbyInfo
    suspend fun getRandomPuzzle(options: PuzzleOptions): Puzzle
    suspend fun getPuzzle(id: String): Puzzle
}
