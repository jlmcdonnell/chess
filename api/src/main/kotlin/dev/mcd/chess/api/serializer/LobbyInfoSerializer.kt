package dev.mcd.chess.api.serializer

import dev.mcd.chess.api.domain.LobbyInfo
import kotlinx.serialization.Serializable

@Serializable
internal data class LobbyInfoSerializer(
    val inLobby: Int,
)

internal fun LobbyInfoSerializer.domain() = LobbyInfo(inLobby)
