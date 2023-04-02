package dev.mcd.chess.feature.game.domain.usecase

interface MoveForBot {
    suspend operator fun invoke()
}
