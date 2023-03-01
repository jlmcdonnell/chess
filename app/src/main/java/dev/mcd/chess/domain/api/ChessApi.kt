package dev.mcd.chess.domain.api

import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.domain.game.GameMessage
import dev.mcd.chess.domain.game.SessionId
import dev.mcd.chess.domain.player.UserId
import kotlinx.coroutines.channels.ReceiveChannel

interface ChessApi {
    suspend fun storeToken(token: String)
    suspend fun userId(): UserId?
    suspend fun generateId(): UserId
    suspend fun findGame(): SessionInfo
    suspend fun game(id: SessionId): SessionInfo
    suspend fun gameForUser(): List<SessionInfo>
    suspend fun joinGame(id: SessionId, block: suspend ActiveGame.() -> Unit)
    suspend fun lobbyInfo(): LobbyInfo
    suspend fun clear()
}

interface ActiveGame {
    val incoming: ReceiveChannel<GameMessage>

    suspend fun requestMoveHistory()
    suspend fun resign()
    suspend fun move(move: Move)
}
