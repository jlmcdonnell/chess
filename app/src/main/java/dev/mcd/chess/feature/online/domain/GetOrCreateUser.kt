package dev.mcd.chess.feature.online.domain

import dev.mcd.chess.common.player.UserId

interface GetOrCreateUser {
    suspend operator fun invoke(): UserId
}
