package dev.mcd.chess.feature.puzzle.data.usecase

import dev.mcd.chess.feature.common.domain.AppPreferences
import dev.mcd.chess.feature.puzzle.maxPuzzleRatingRange
import dev.mcd.chess.feature.puzzle.domain.usecase.GetPuzzleOptions
import dev.mcd.chess.online.domain.PuzzleOptions
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class GetPuzzleOptionsImpl @Inject constructor(
    private val appPreferences: AppPreferences,
) : GetPuzzleOptions {

    override suspend fun invoke(): PuzzleOptions {
        val (ratingMin, ratingMax) = appPreferences.puzzleRatingRange().let { it.first to it.last }
        val coercedMin = max(maxPuzzleRatingRange.first, ratingMin)
        val coercedMax = max(coercedMin, min(maxPuzzleRatingRange.last, ratingMax))
        return PuzzleOptions(
            coercedMin..coercedMax,
        )
    }
}
