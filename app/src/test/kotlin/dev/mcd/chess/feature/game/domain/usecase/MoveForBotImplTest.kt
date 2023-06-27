package dev.mcd.chess.feature.game.domain.usecase

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.feature.engine.BotEngineProxy
import dev.mcd.chess.feature.game.data.GameSessionRepositoryImpl
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class MoveForBotImplTest : BehaviorSpec(
    {
        isolationMode = IsolationMode.InstancePerLeaf
        coroutineTestScope = true

        val sessionRepository = GameSessionRepositoryImpl()
        val engine = mockk<BotEngineProxy>()
        val moveForBot = MoveForBotImpl(sessionRepository, engine)

        Given("no active game") {
            Then("throw exception") {
                shouldThrow<Exception> { moveForBot() }
            }
        }
        Given("an active game") {
            val game = GameSession()
            game.setBoard(Board())
            sessionRepository.updateActiveGame(game)

            And("the game is terminated") {
                game.resign()

                When("invoked") {
                    moveForBot()

                    Then("return") {
                        coVerify(exactly = 0) { engine.getMove(any()) }
                    }
                }
            }
            And("it is not terminated") {
                val move = "e2e4"
                coEvery { engine.getMove(any()) } returns move

                When("invoked") {
                    moveForBot()

                    Then("update the game with an engine move") {
                        game.lastMove()?.move?.move shouldBe Move(Square.E2, Square.E4)
                    }
                }
            }
        }
    },
)
