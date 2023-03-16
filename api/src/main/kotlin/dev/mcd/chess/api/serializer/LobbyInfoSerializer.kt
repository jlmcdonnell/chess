package dev.mcd.chess.api.serializer

import dev.mcd.chess.api.domain.LobbyInfo
import kotlinx.serialization.Serializable

@Serializable
data class LobbyInfoSerializer(
    val inLobby: Int,
)

fun LobbyInfoSerializer.domain() = LobbyInfo(inLobby)
