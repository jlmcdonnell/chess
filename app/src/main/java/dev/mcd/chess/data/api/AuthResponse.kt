package dev.mcd.chess.data.api

import dev.mcd.chess.domain.player.UserId
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val userId: UserId,
)
