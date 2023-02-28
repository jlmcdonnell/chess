package dev.mcd.chess.data.api

import dev.mcd.chess.data.api.serializer.GameMessageSerializer
import dev.mcd.chess.data.api.serializer.MessageType
import dev.mcd.chess.data.api.serializer.asMove
import dev.mcd.chess.data.api.serializer.asMoveHistory
import dev.mcd.chess.data.api.serializer.asSessionInfo
import dev.mcd.chess.domain.game.GameMessage
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.serialization.decodeFromString

fun Frame.Text.gameMessage(): GameMessage {
    val frameText = readText()
    val data = DefaultJson.decodeFromString<GameMessageSerializer>(frameText)

    return when (data.message) {
        MessageType.SessionInfo -> data.asSessionInfo()
        MessageType.MoveHistory -> data.asMoveHistory()
        MessageType.Move -> data.asMove()
        MessageType.ErrorNotUsersMove -> GameMessage.ErrorNotUsersMove
        MessageType.ErrorGameTerminated -> GameMessage.ErrorGameTerminated
    }
}
