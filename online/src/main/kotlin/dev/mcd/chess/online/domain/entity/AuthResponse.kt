package dev.mcd.chess.online.domain.entity

import dev.mcd.chess.common.player.UserId

data class AuthResponse(
    val token: String,
    val userId: UserId,
)
