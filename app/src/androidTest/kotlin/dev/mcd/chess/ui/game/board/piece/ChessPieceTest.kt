package dev.mcd.chess.ui.game.board.piece

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import dev.mcd.chess.TestBoardSounds
import dev.mcd.chess.TestGameSessionRepository
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.test.createGameSessionRule
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.game.board.chessboard.ChessBoard
import dev.mcd.chess.ui.game.board.chessboard.ChessBoardViewModel
import dev.mcd.chess.ui.game.board.interaction.BoardInteraction
import dev.mcd.chess.ui.theme.ChessTheme
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChessPieceTest {

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val gameRule = createGameSessionRule()

    @Before
    fun setUp() {
        with(composeRule) {
            mainClock.autoAdvance = false
            setContent {
                TestChessBoard(gameRule.game)
            }
        }
    }

    @Test
    fun piecesRespondToBoardMoves(): Unit = runBlocking {
        listOf("e2e4", "e7e5").forEach { move ->
            composeRule.move(move)
        }

        with(composeRule) {
            onNodeWithContentDescription("E4").assertExists()
            onNodeWithContentDescription("E5").assertExists()
            onNodeWithContentDescription("E2").assertDoesNotExist()
        }
    }

    @Test
    fun pieceHighlighting(): Unit = runBlocking {
        with(composeRule) {
            onNodeWithTag("highlight-from").assertDoesNotExist()
            onNodeWithTag("highlight-to").assertDoesNotExist()

            move("e2e4")
            mainClock.advanceTimeBy(200)

            onNodeWithTag("highlight-from").assertExists()
            onNodeWithTag("highlight-to").assertExists()
        }
    }

    private suspend fun ComposeTestRule.move(move: String) {
        gameRule.game.move(move)
        mainClock.advanceTimeBy(200)
    }

    @Test
    fun pieceCapture(): Unit = runBlocking {
        with(composeRule) {
            move("e2e4")
            move("d7d5")
            move("e4d5")

            onNodeWithContentDescription("D5").assertExists()
            onNodeWithContentDescription("E4").assertDoesNotExist()
        }
    }

    @Composable
    private fun TestChessBoard(game: GameSession) {
        val sessionManager = LocalGameSession.current

        LaunchedEffect(Unit) {
            sessionManager.updateSession(game)
        }

        ChessTheme {
            Surface {
                Box(Modifier.fillMaxSize()) {
                    val boardViewModel = ChessBoardViewModel(TestBoardSounds(), TestGameSessionRepository())
                    val boardInteraction = BoardInteraction(game)

                    CompositionLocalProvider(LocalBoardInteraction provides boardInteraction) {
                        ChessBoard(
                            gameId = game.id,
                            viewModel = boardViewModel,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}
