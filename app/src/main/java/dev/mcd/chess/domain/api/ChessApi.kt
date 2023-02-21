package dev.mcd.chess.domain.api

import dev.mcd.chess.domain.game.GameMessage
import dev.mcd.chess.domain.game.SessionId
import dev.mcd.chess.domain.player.UserId
import kotlinx.coroutines.channels.ReceiveChannel

interface ChessApi {
    suspend fun userId(): UserId?
    suspend fun generateId(): UserId
    suspend fun findGame(): SessionId
    suspend fun joinGame(id: SessionId, block: suspend ActiveGame.() -> Unit)
}

interface ActiveGame {
    val incoming: ReceiveChannel<GameMessage>
    suspend fun send(move: String)
}