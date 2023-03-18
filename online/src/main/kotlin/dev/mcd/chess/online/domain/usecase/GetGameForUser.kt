package dev.mcd.chess.online.domain.usecase

import dev.mcd.chess.common.game.GameId

interface GetGameForUser {
    suspend operator fun invoke(): GameId?
}
