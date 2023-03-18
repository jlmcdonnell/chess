package dev.mcd.chess.online.data.mapper

import dev.mcd.chess.online.data.serializer.GameMessageSerializer
import dev.mcd.chess.online.data.serializer.MessageType
import dev.mcd.chess.online.data.serializer.asGameState
import dev.mcd.chess.online.data.serializer.asMove
import dev.mcd.chess.online.domain.entity.GameMessage
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
