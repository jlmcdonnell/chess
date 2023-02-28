package dev.mcd.chess.data.api.serializer

import androidx.annotation.Keep
import dev.mcd.chess.domain.game.GameMessage
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

@Serializable
@Keep
data class GameMessageSerializer(
    val message: MessageType,
    val content: String? = null,
)

inline fun <reified T> GameMessageSerializer.decodeContent(): T =
    DefaultJson.decodeFromString(content!!.replace("\\", ""))

fun GameMessageSerializer.asSessionInfo(): GameMessage.SessionInfoMessage {
    require(message == MessageType.SessionInfo)
    val serializer = decodeContent<SessionInfoSerializer>()
    return GameMessage.SessionInfoMessage(
        sessionInfo = serializer.domain(),
    )
}

fun GameMessageSerializer.asMoveHistory(): GameMessage.MoveHistoryMessage {
    require(message == MessageType.MoveHistory)
    val serializer = decodeContent<MoveHistorySerializer>()
    return GameMessage.MoveHistoryMessage(
        moveHistory = serializer.domain(),
    )
}

fun GameMessageSerializer.asMove(): GameMessage.MoveMessage {
    require(message == MessageType.Move)
    val serializer = decodeContent<MoveSerializer>()
    return GameMessage.MoveMessage(
        move = serializer.move
    )
}

@Serializable
@Keep
enum class MessageType {
    SessionInfo,
    MoveHistory,
    Move,
    ErrorNotUsersMove,
    ErrorGameTerminated,
}
