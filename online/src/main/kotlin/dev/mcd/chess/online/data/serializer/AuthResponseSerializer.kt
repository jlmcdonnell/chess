package dev.mcd.chess.online.data.serializer

import dev.mcd.chess.common.player.UserId
import dev.mcd.chess.online.domain.entity.AuthResponse
import kotlinx.serialization.Serializable

@Serializable
internal data class AuthResponseSerializer(
    val token: String,
    val userId: UserId,
)

internal fun AuthResponseSerializer.domain() = AuthResponse(token, userId)
