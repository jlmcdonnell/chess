package dev.mcd.chess.domain

import dev.mcd.chess.common.game.online.GameSession

interface FindGame {
    suspend operator fun invoke(): GameSession
}
