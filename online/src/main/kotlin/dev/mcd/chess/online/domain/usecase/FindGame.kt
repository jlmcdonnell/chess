package dev.mcd.chess.online.domain.usecase

import dev.mcd.chess.common.game.GameId

interface FindGame {
    suspend operator fun invoke(): GameId
}
