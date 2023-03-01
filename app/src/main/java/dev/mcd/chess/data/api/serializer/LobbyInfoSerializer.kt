package dev.mcd.chess.data.api.serializer

import dev.mcd.chess.domain.api.LobbyInfo
import kotlinx.serialization.Serializable

@Serializable
data class LobbyInfoSerializer(
    val inLobby: Int,
)

fun LobbyInfoSerializer.domain() = LobbyInfo(inLobby)
