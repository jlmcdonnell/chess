package dev.mcd.chess.feature.online.domain

import dev.mcd.chess.api.domain.GameSession

interface FindGame {
    suspend operator fun invoke(): GameSession
}
