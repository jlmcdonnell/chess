package dev.mcd.chess.data.api.serializer

import androidx.annotation.Keep
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.domain.game.BoardState
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

fun GameMessageSerializer.asBoardState(): GameMessage.BoardStateMessage {
    require(message == MessageType.BoardState)
    val contentJson = content!!.replace("\\", "")
    val stateSerializer = DefaultJson.decodeFromString<BoardStateSerializer>(contentJson)

    return GameMessage.BoardStateMessage(
        state = BoardState(
            fen = stateSerializer.fen,
            lastMoveSan = stateSerializer.lastMoveSan,
            lastMoveSide = stateSerializer.lastMoveSide?.let(Side::valueOf),
            moveCount = stateSerializer.moveCount,
        )
    )
}

@Serializable
@Keep
enum class MessageType {
    BoardState,
    ErrorNotUsersMove,
    GameTermination,
}
