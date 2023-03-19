package dev.mcd.chess.online.data.serializer

import dev.mcd.chess.online.domain.entity.GameMessage
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

@Serializable
internal data class GameMessageSerializer(
    val message: MessageType,
    val content: String? = null,
)

internal inline fun <reified T> GameMessageSerializer.decodeContent(): T =
    DefaultJson.decodeFromString(content!!.replace("\\", ""))

internal fun GameMessageSerializer.asGameState(): GameMessage.GameState {
    require(message == MessageType.GameState)
    val serializer = decodeContent<GameStateMessageSerializer>()
    return serializer.domain()
}

internal fun GameMessageSerializer.asMove(): GameMessage.MoveMessage {
    require(message == MessageType.Move)
    val serializer = decodeContent<MoveMessageSerializer>()
    return GameMessage.MoveMessage(
        move = serializer.move,
        count = serializer.count,
    )
}

@Serializable
internal enum class MessageType {
    GameState,
    Move,
    ErrorNotUsersMove,
    ErrorGameTerminated,
    ErrorInvalidMove,
}
