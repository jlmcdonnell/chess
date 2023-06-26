package dev.mcd.chess.feature.puzzle.data.usecase

import app.cash.turbine.test
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.feature.common.domain.Translations
import dev.mcd.chess.feature.game.data.GameSessionRepositoryImpl
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession.DelaySettings
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession.PuzzleOutput.Completed
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession.PuzzleOutput.Failed
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession.PuzzleOutput.PlayerToMove
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession.PuzzleOutput.Session
import dev.mcd.chess.online.domain.entity.Puzzle
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class CreatePuzzleSessionImplTest : BehaviorSpec(
    {
        isolationMode = IsolationMode.InstancePerLeaf
        coroutineTestScope = true

        val sessionRepository = GameSessionRepositoryImpl()
        val translations = mockk<Translations> {
            every { playerYou } returns "You"
            every { playerPuzzle(any()) } returns "puzzle"
        }
        val sut = CreatePuzzleSessionImpl(
            gameSessionRepository = sessionRepository,
            translations = translations,
        )

        Given("A puzzle") {
            val (input, output) = sut(testPuzzle, zeroDelay)

            output.test {
                val sessionOutput = awaitItem() as Session

                When("invoked") {
                    Then("the first move is made") {
                        val lastMove = sessionOutput.session.lastMove()?.move?.move
                        lastMove shouldBe Move(Square.D8, Square.C6)
                        cancelAndConsumeRemainingEvents()
                    }
                    Then("the repository is updated") {
                        sessionRepository.activeGame().value shouldBe sessionOutput.session
                        cancelAndConsumeRemainingEvents()
                    }
                }
                When("the correct moves are made") {
                    input.move("e5e1")
                    input.move("d2e1q")
                    Then("the puzzle should complete") {
                        awaitItem() shouldBe PlayerToMove
                        awaitItem() shouldBe PlayerToMove
                        awaitItem() shouldBe Completed
                    }
                }
                When("an alternate checkmate is found") {
                    input.move("e5e1")
                    input.move("d2e1r")
                    Then("the puzzle should complete") {
                        awaitItem() shouldBe PlayerToMove
                        awaitItem() shouldBe PlayerToMove
                        awaitItem() shouldBe Completed
                    }
                }
                When("the incorrect move is made") {
                    input.move("e5e2")
                    Then("the puzzle is failed") {
                        awaitItem() shouldBe PlayerToMove
                        awaitItem() shouldBe Failed
                    }
                    And("the player retries the puzzle correctly") {
                        input.retry()
                        input.move("e5e1")
                        Then("the move is correct") {
                            awaitItem() shouldBe PlayerToMove
                            awaitItem() shouldBe Failed
                            awaitItem() shouldBe PlayerToMove
                        }
                    }
                }
            }
        }
    },
)

private val zeroDelay = DelaySettings(
    beforePuzzleStartDelay = 0,
    afterPlayerMoveDelay = 0,
)

private val testPuzzle = Puzzle(
    puzzleId = "Bmrpm",
    fen = "3N4/7k/R7/p3r2p/Pp6/2b5/3p1PPP/3R2K1 w - - 2 45",
    moves = listOf(
        "d8c6",
        "e5e1", // move 1/2
        "d1e1",
        "d2e1q", // move 2/2
    ),
    rating = 0,
    themes = listOf("sadge"),
)
