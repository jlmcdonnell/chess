package dev.mcd.chess.online.data.serializer

import dev.mcd.chess.online.domain.entity.LobbyInfo
import kotlinx.serialization.Serializable

@Serializable
internal data class LobbyInfoSerializer(
    val inLobby: Int,
)

internal fun LobbyInfoSerializer.domain() = LobbyInfo(inLobby)
