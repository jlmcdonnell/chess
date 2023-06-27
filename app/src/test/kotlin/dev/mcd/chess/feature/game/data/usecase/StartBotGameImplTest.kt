package dev.mcd.chess.feature.game.data.usecase

import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.common.player.Bot
import dev.mcd.chess.common.player.PlayerImage
import dev.mcd.chess.feature.common.domain.Translations
import dev.mcd.chess.feature.engine.BotEngineProxy
import dev.mcd.chess.feature.game.data.GameSessionRepositoryImpl
import dev.mcd.chess.feature.game.domain.usecase.MoveForBotImpl
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk

class StartBotGameImplTest : BehaviorSpec(
    {
        val playerName = "player-name"
        val engine = mockk<BotEngineProxy> {
            coEvery { getMove(any()) } returns "e2e4"
        }
        val translations = mockk<Translations> {
            every { playerYou } returns playerName
        }
        val sessionRepository = GameSessionRepositoryImpl()
        val moveForBot = MoveForBotImpl(sessionRepository, engine)
        val sut = StartBotGameImpl(sessionRepository, moveForBot, translations)

        Given("a bot") {
            val bot = Bot(name = "testBot", PlayerImage.Bot, "testBotSlug")

            And("it plays as white") {
                val playerSide = Side.BLACK

                When("StartBotGame is invoked") {
                    sut(playerSide, bot)

                    Then("the session repository is updated with the game") {
                        sessionRepository.activeGame().value.shouldNotBeNull()
                    }
                    Then("a move is played for the bot") {
                        sessionRepository.activeGame().value!!.lastMove().shouldNotBeNull()
                    }
                }
            }
            And("it plays as black") {
                val playerSide = Side.WHITE

                When("StartBotGame is invoked") {
                    sut(playerSide, bot)

                    Then("no move is played for the bot") {
                        sessionRepository.activeGame().value?.lastMove().shouldBeNull()
                    }
                }
            }

            listOf(Side.WHITE, Side.BLACK).forEach { side ->
                And("the player is playing ${side.name.lowercase()}") {
                    When("StartBotGame is invoked") {
                        sut(side, bot)

                        Then("the side is set") {
                            sessionRepository.activeGame().value?.selfSide shouldBe side
                        }
                    }
                }
            }

            When("StartBotGame is invoked") {
                sut(Side.WHITE, bot)

                Then("the player name is set") {
                    sessionRepository.activeGame().value?.self?.name shouldBe playerName
                }
                Then("the bot name is set") {
                    sessionRepository.activeGame().value?.opponent?.name shouldBe bot.name
                }
            }
        }
    },
)
