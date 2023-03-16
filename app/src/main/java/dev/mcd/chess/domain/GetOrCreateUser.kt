package dev.mcd.chess.domain

import dev.mcd.chess.common.player.UserId

interface GetOrCreateUser {
    suspend operator fun invoke(): UserId
}
