package dev.mcd.chess.data.api

import androidx.annotation.Keep
import dev.mcd.chess.domain.player.UserId
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class AuthResponse(
    val token: String,
    val userId: UserId,
)
