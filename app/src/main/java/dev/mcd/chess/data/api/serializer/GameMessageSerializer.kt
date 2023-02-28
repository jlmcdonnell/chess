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

fun GameMessageSerializer.asSessionInfo(): GameMessage.SessionInfoMessage {
    require(message == MessageType.SessionInfo)
    val contentJson = content!!.replace("\\", "")
    val sessionInfoSerializer = DefaultJson.decodeFromString<SessionInfoSerializer>(contentJson)

    return GameMessage.SessionInfoMessage(
        sessionInfo = sessionInfoSerializer.domain(),
    )
}

@Serializable
@Keep
enum class MessageType {
    SessionInfo,
    ErrorNotUsersMove,
    ErrorGameTerminated,
}
