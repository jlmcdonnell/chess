package dev.mcd.chess.api.serializer

import dev.mcd.chess.api.domain.AuthResponse
import dev.mcd.chess.common.player.UserId
import kotlinx.serialization.Serializable

@Serializable
internal data class AuthResponseSerializer(
    val token: String,
    val userId: UserId,
)

internal fun AuthResponseSerializer.domain() = AuthResponse(token, userId)
