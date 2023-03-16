package dev.mcd.chess.api.data

import dev.mcd.chess.common.player.UserId
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val userId: UserId,
)
