package dev.mcd.chess.api.data

import dev.mcd.chess.api.domain.GameMessage
import dev.mcd.chess.api.serializer.GameMessageSerializer
import dev.mcd.chess.api.serializer.MessageType
import dev.mcd.chess.api.serializer.asGameState
import dev.mcd.chess.api.serializer.asMove
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.serialization.decodeFromString

internal fun Frame.Text.gameMessage(): GameMessage {
    val frameText = readText()
    val data = DefaultJson.decodeFromString<GameMessageSerializer>(frameText)

    return when (data.message) {
        MessageType.GameState -> data.asGameState()
        MessageType.Move -> data.asMove()
        MessageType.ErrorNotUsersMove -> GameMessage.ErrorNotUsersMove
        MessageType.ErrorGameTerminated -> GameMessage.ErrorGameTerminated
        MessageType.ErrorInvalidMove -> GameMessage.ErrorInvalidMove
    }
}
