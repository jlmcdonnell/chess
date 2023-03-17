package dev.mcd.chess.domain

import dev.mcd.chess.api.domain.GameSession


interface FindGame {
    suspend operator fun invoke(): GameSession
}
