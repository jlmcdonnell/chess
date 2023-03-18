package dev.mcd.chess.common.domain

import dev.mcd.chess.common.player.UserId

interface GetOrCreateUser {
    suspend operator fun invoke(): UserId
}
