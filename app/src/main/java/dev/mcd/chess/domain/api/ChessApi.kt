package dev.mcd.chess.domain.api

import dev.mcd.chess.domain.game.GameMessage
import dev.mcd.chess.domain.game.SessionId
import dev.mcd.chess.domain.player.UserId
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

interface ChessApi {
    suspend fun storeToken(token: String)
    suspend fun userId(): UserId?
    suspend fun generateId(): UserId
    suspend fun findGame(): SessionInfo
    suspend fun session(id: SessionId): SessionInfo
    suspend fun joinGame(id: SessionId, block: suspend ActiveGame.() -> Unit)
}

class ActiveGame(
    val incoming: ReceiveChannel<GameMessage>,
    val outgoing: SendChannel<String>,
)
