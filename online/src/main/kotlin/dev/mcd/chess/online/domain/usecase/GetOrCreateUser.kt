package dev.mcd.chess.online.domain.usecase

import dev.mcd.chess.common.player.UserId

interface GetOrCreateUser {
    suspend operator fun invoke(): UserId
}
