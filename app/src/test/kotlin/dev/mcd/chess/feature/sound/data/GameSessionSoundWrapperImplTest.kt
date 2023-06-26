package dev.mcd.chess.feature.sound.data

import com.github.bhlangonijr.chesslib.Board
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.feature.sound.domain.BoardSoundPlayer
import dev.mcd.chess.feature.sound.domain.SoundSettings
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerScope
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.withContext

class GameSessionSoundWrapperImplTest : BehaviorSpec(
    {
        isolationMode = IsolationMode.InstancePerLeaf

        Given("sound is disabled") {
            withSoundWrapperTest {
                givenSoundSettings(enableNotify = true, enabled = false)
                givenDefaultBoard()
                attachSession()
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
        }

        Given("enableNotify is true") {
            withSoundWrapperTest {
                givenSoundSettings(enableNotify = true, enabled = true)
                attachSession()
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
        }

        Given("enableNotify is false") {
            withSoundWrapperTest {
                givenSoundSettings(enableNotify = false, enabled = true)
                attachSession()
                scope.advanceUntilIdle()

                Then("don't play notify") {
                    coVerify(exactly = 0) { soundPlayer.playNotify() }
                }
            }
        }

        Given("a move is made") {
            withSoundWrapperTest {
                givenSoundSettings(enableNotify = false, enabled = true)
                givenDefaultBoard()

                And("there is no piece captured") {
                    attachSession()
                    session.move("e2e4")
                    scope.advanceUntilIdle()

                    Then("play move sound") {
                        coVerify { soundPlayer.playMove() }
                    }
                }
                And("a piece is captured") {
                    attachSession()
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
        }
    },
)

context(BehaviorSpecGivenContainerScope)
private inline fun withSoundWrapperTest(block: SoundWrapperTestScope.() -> Unit) {
    block(SoundWrapperTestScope())
}

private class SoundWrapperTestScope {
    val scope = TestScope()
    var settings: SoundSettings = SoundSettings()
    var session: GameSession = GameSession()
    val soundPlayer = mockk<BoardSoundPlayer> {
        coEvery { playNotify() } just Runs
        coEvery { playMove() } just Runs
        coEvery { playCapture() } just Runs
    }

    val sut = GameSessionSoundWrapperImpl(context = scope.coroutineContext, soundPlayer)

    suspend fun givenDefaultBoard() {
        session.setBoard(Board())
    }

    fun givenSoundSettings(enabled: Boolean, enableNotify: Boolean) {
        settings = settings.copy(enabled = enabled, enableNotify = enableNotify)
    }

    suspend fun attachSession() {
        withContext(Dispatchers.Unconfined) {
            sut.attachSession(session, settings)
        }
    }
}
