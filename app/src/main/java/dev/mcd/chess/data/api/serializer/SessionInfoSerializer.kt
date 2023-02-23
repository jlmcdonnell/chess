package dev.mcd.chess.data.api.serializer

import androidx.annotation.Keep
import dev.mcd.chess.domain.api.SessionInfo
import dev.mcd.chess.domain.game.SessionId
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class SessionInfoSerializer(
    val sessionId: SessionId,
    val whiteUserId: String,
    val blackUserId: String,
    val state: State,
    val board: BoardStateSerializer,
)

fun SessionInfoSerializer.domain() = SessionInfo(
    sessionId = sessionId,
    whiteUserId = whiteUserId,
    blackUserId = blackUserId,
    state = state.domain(),
    board = board.toBoardState(),
)
