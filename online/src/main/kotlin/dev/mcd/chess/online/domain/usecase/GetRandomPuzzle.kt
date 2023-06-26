package dev.mcd.chess.online.domain.usecase

import dev.mcd.chess.online.domain.entity.Puzzle

interface GetRandomPuzzle {
    suspend operator fun invoke(ratingRange: IntRange): Puzzle
}
