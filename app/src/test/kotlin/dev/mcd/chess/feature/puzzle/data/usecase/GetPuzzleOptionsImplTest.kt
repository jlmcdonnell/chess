package dev.mcd.chess.feature.puzzle.data.usecase

import dev.mcd.chess.feature.common.domain.AppPreferences
import dev.mcd.chess.feature.puzzle.maxPuzzleRatingRange
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk

class GetPuzzleOptionsImplTest : StringSpec(
    {
        isolationMode = IsolationMode.InstancePerLeaf

        val preferences = mockk<AppPreferences>()
        val sut = GetPuzzleOptionsImpl(preferences)
        val minMin = maxPuzzleRatingRange.first
        val maxMax = maxPuzzleRatingRange.last

        "coerce min rating to allowed min rating" {
            coEvery { preferences.puzzleRatingRange() } returns (minMin - 1)..maxMax
            sut().ratingRange shouldBe minMin..maxMax
        }

        "coerce max rating to coerced min rating" {
            val max = minMin - 1
            coEvery { preferences.puzzleRatingRange() } returns minMin..max
            sut().ratingRange shouldBe minMin..minMin
        }

        "coerce max rating to allowed max rating" {
            coEvery { preferences.puzzleRatingRange() } returns minMin..(maxMax + 1)
            sut().ratingRange shouldBe minMin..maxMax
        }

        "allowable rating range preference" {
            val min = minMin + 1
            val max = maxMax - 1
            coEvery { preferences.puzzleRatingRange() } returns min..max
            sut().ratingRange shouldBe min..max
        }
    },
)
