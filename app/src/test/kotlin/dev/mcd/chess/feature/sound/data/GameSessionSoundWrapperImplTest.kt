package dev.mcd.chess.feature.sound.data

import com.github.bhlangonijr.chesslib.Board
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.feature.sound.domain.BoardSoundPlayer
import dev.mcd.chess.feature.sound.domain.SoundSettings
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle

class GameSessionSoundWrapperImplTest : BehaviorSpec(
    {
        isolationMode = IsolationMode.InstancePerLeaf

        val scope = TestScope()
        val soundPlayer = mockk<BoardSoundPlayer> {
            coEvery { playNotify() } just Runs
        }
        val sut = GameSessionSoundWrapperImpl(scope = scope, soundPlayer)

        Given("sound is disabled") {
            val settings = SoundSettings(enableNotify = true, enabled = false)
            val session = GameSession().apply { setBoard(Board()) }
            sut.attachSession(session, settings)
            scope.advanceUntilIdle()

            When("the session starts") {
                Then("no sound is played") {
                    coVerify(exactly = 0) { soundPlayer.playNotify() }
                }
            }
            When("the session terminates") {
                session.resign()
                scope.advanceUntilIdle()

                Then("no sound is played") {
                    coVerify(exactly = 0) { soundPlayer.playNotify() }
                }
            }
            When("a move is made") {
                session.apply {
                    move("e2e4")
                }
                Then("no sound is played") {
                    coVerify(exactly = 0) { soundPlayer.playMove() }
                }
            }
            When("a capture is made") {
                session.apply {
                    move("e2e4")
                    move("d7d5")
                    move("e4d5")
                }
                Then("no sound is played") {
                    coVerify(exactly = 0) { soundPlayer.playCapture() }
                }
            }
        }

        Given("enableNotify is true") {
            val settings = SoundSettings(enableNotify = true, enabled = true)
            val session = GameSession()
            sut.attachSession(session, settings)
            scope.advanceUntilIdle()

            Then("play notify when session attached") {
                coVerify { soundPlayer.playNotify() }
            }

            When("the session terminates") {
                session.resign()
                scope.advanceUntilIdle()

                Then("play notify after session terminates") {
                    coVerify(exactly = 2) { soundPlayer.playNotify() }
                }
            }
        }

        Given("enableNotify is false") {
            val settings = SoundSettings(enableNotify = false, enabled = true)
            val session = GameSession()
            sut.attachSession(session, settings)
            scope.advanceUntilIdle()

            Then("don't play notify") {
                coVerify(exactly = 0) { soundPlayer.playNotify() }
            }
        }

        Given("a move is made") {
            val settings = SoundSettings(enableNotify = false, enabled = true)
            val session = GameSession().apply {
                setBoard(Board())
            }

            And("there is no piece captured") {
                sut.attachSession(session, settings)
                session.move("e2e4")
                scope.advanceUntilIdle()

                Then("play move sound") {
                    coVerify { soundPlayer.playMove() }
                }
            }
            And("a piece is captured") {
                sut.attachSession(session, settings)
                session.apply {
                    move("e2e4")
                    move("d7d5")
                    move("e4d5")
                }
                scope.advanceUntilIdle()

                Then("play capture sound") {
                    coVerify { soundPlayer.playCapture() }
                }
            }
        }
    },
)
