package dev.mcd.chess.data.api

import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.domain.game.GameMessage
import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

@Serializable
data class GameMessageSerializer(
    val message: MessageType,
    val content: String? = null,
)

fun GameMessageSerializer.asBoardState(): GameMessage.BoardState {
    require(message == MessageType.BoardState)

    val contentJson = content!!.replace("\\", "")
    val stateSerializer = DefaultJson.decodeFromString<BoardStateSerializer>(contentJson)
    return GameMessage.BoardState(
        side = Side.valueOf(stateSerializer.side),
        fen = stateSerializer.fen,
        lastMoveSan = stateSerializer.lastMoveSan,
        lastMoveSide = stateSerializer.lastMoveSide,
        plyCount = stateSerializer.plyCount,
        moveCount = stateSerializer.moveCount,
    )
}

@Serializable
enum class MessageType {
    BoardState,
    ErrorNotUsersMove,
    GameTermination,
}