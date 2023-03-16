package dev.mcd.chess.api.domain

import dev.mcd.chess.common.player.UserId

data class AuthResponse(
    val token: String,
    val userId: UserId,
)
