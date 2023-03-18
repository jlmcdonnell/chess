package dev.mcd.chess.online.domain.usecase

import dev.mcd.chess.online.domain.entity.GameSession

interface FindGame {
    suspend operator fun invoke(): GameSession
}
