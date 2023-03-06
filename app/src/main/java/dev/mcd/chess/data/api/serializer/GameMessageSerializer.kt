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

fun GameMessageSerializer.asGameState(): GameMessage.GameState {
    require(message == MessageType.GameState)
    val serializer = decodeContent<GameStateMessageSerializer>()
    return GameMessage.GameState(
        session = serializer.domain(),
    )
}

fun GameMessageSerializer.asMove(): GameMessage.MoveMessage {
    require(message == MessageType.Move)
    val serializer = decodeContent<MoveMessageSerializer>()
    return GameMessage.MoveMessage(
        move = serializer.move,
        count = serializer.count,
    )
}

@Serializable
@Keep
enum class MessageType {
    GameState,
    Move,
    ErrorNotUsersMove,
    ErrorGameTerminated,
    ErrorInvalidMove,
}
