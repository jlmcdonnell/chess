package dev.mcd.chess.feature.puzzle.domain.usecase

import dev.mcd.chess.online.domain.PuzzleOptions

interface GetPuzzleOptions {
    suspend operator fun invoke(): PuzzleOptions
}
