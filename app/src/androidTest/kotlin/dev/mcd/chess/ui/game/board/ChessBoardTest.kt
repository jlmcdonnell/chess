package dev.mcd.chess.ui.game.board

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Piece.BLACK_BISHOP
import com.github.bhlangonijr.chesslib.Piece.BLACK_PAWN
import com.github.bhlangonijr.chesslib.Piece.BLACK_ROOK
import com.github.bhlangonijr.chesslib.Piece.WHITE_PAWN
import com.github.bhlangonijr.chesslib.Piece.WHITE_QUEEN
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.Square.B7
import com.github.bhlangonijr.chesslib.Square.D1
import com.github.bhlangonijr.chesslib.Square.D5
import com.github.bhlangonijr.chesslib.Square.D7
import com.github.bhlangonijr.chesslib.Square.E2
import com.github.bhlangonijr.chesslib.Square.E4
import com.github.bhlangonijr.chesslib.Square.E5
import com.github.bhlangonijr.chesslib.Square.E7
import com.github.bhlangonijr.chesslib.Square.F8
import com.github.bhlangonijr.chesslib.Square.G7
import com.github.bhlangonijr.chesslib.Square.H5
import com.github.bhlangonijr.chesslib.Square.H8
import dev.mcd.chess.TestBoardSounds
import dev.mcd.chess.TestGameSessionRepository
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.test.createGameSessionRule
import dev.mcd.chess.ui.LocalBoardInteraction
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.game.board.chessboard.ChessBoard
import dev.mcd.chess.ui.game.board.chessboard.ChessBoardViewModel
import dev.mcd.chess.ui.game.board.interaction.BoardInteraction
import dev.mcd.chess.ui.game.board.piece.PieceSquare
import dev.mcd.chess.ui.game.board.piece.PieceSquareKey
import dev.mcd.chess.ui.theme.ChessTheme
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChessBoardTest {

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
            mainClock.advanceTimeByFrame()
        }
    }

    @Test
    fun piecesRespondToBoardMoves(): Unit = runBlocking {
        with(composeRule) {
            mainClock.advanceTimeByFrame()

            assertPiece(WHITE_PAWN on E2)
            assertPiece(BLACK_PAWN on E7)

            listOf("e2e4", "e7e5").forEach { move ->
                move(move)
            }

            assertPiece(WHITE_PAWN on E4)
            assertPiece(BLACK_PAWN on E5)
            assertNoPiece(WHITE_PAWN on E2)
            assertNoPiece(BLACK_PAWN on E7)
        }
    }

    @Test
    fun pieceHighlighting(): Unit = runBlocking {
        with(composeRule) {
            onNodeWithTag("highlight-from").assertDoesNotExist()
            onNodeWithTag("highlight-to").assertDoesNotExist()

            move("e2e4")

            onNodeWithTag("highlight-from").assertExists()
            onNodeWithTag("highlight-to").assertExists()
        }
    }

    @Test
    fun pieceCapture(): Unit = runBlocking {
        with(composeRule) {
            move("e2e4")
            move("d7d5")
            move("e4d5")

            assertPiece(WHITE_PAWN on D5)
            assertNoPiece(BLACK_PAWN on D5)
        }
    }

    @Test
    fun undoMove(): Unit = runBlocking {
        with(composeRule) {
            move("e2e4")

            assertPiece(WHITE_PAWN on E4)

            undoMove()

            assertPiece(WHITE_PAWN on E2)
            assertNoPiece(WHITE_PAWN on E4)
        }
    }

    @Test
    fun undoMoveAfterCapture(): Unit = runBlocking {
        with(composeRule) {
            move("e2e4")
            move("d7d5")
            move("e4d5")

            assertPiece(WHITE_PAWN on D5)

            undoMove()

            assertPiece(BLACK_PAWN on D5)
            assertPiece(WHITE_PAWN on E4)
        }
    }

    @Test
    fun undoMoveAfterSeveralCaptures(): Unit = runBlocking {
        with(composeRule) {
            move("e2e4")
            move("e7e5")
            move("d1h5")
            move("g7g6")
            move("h5e5")
            move("f8e7")
            move("e5h8")

            undoMove()
            assertPiece(
                BLACK_ROOK on H8,
                WHITE_QUEEN on E5
            )

            undoMove()
            assertPiece(
                WHITE_QUEEN on E5,
                BLACK_BISHOP on F8,
            )

            undoMove()
            assertPiece(
                WHITE_QUEEN on H5,
                BLACK_PAWN on E5,
            )

            undoMove()
            assertPiece(BLACK_PAWN on G7)

            undoMove()
            assertPiece(WHITE_QUEEN on D1)

            undoMove()
            assertPiece(BLACK_PAWN on E7)

            undoMove()
            assertPiece(WHITE_PAWN on E2)

            mainClock.advanceTimeBy(1000)
        }
    }

    @Test
    fun undoMoveAfterSeveralCapturesOnSameSquare(): Unit = runBlocking {
        // Context: This would fail before ChessPieceState cached the moves for a piece.

        with(composeRule) {
            val moves =
                listOf("e2e4", "e7e6", "b1c3", "b8c6", "f1b5", "a7a6", "b5c6", "b7c6", "d1h5", "d7d5", "e4d5", "c6d5", "c3d5", "d8d5", "h5d5", "e6d5")

            moves.forEach {
                move(it)
            }

            repeat(moves.size) {
                undoMove()
            }

            assertPiece(
                BLACK_PAWN on B7,
                BLACK_PAWN on D7,
            )
        }
    }

    private suspend fun ComposeTestRule.move(move: String) {
        gameRule.game.move(move)
        mainClock.advanceTimeBy(1000)
    }

    private fun ComposeTestRule.undoMove() {
        gameRule.game.undo()
        mainClock.advanceTimeBy(1000)
    }

    private fun ComposeTestRule.assertPiece(vararg pieceSquare: PieceSquare) {
        pieceSquare.forEach {
            onNode(SemanticsMatcher.expectValue(PieceSquareKey, it)).assertExists()
        }
    }

    private fun ComposeTestRule.assertNoPiece(vararg pieceSquare: PieceSquare) {
        pieceSquare.forEach {
            onNode(SemanticsMatcher.expectValue(PieceSquareKey, it)).assertDoesNotExist()
        }
    }

    private infix fun Piece.on(square: Square): PieceSquare = PieceSquare(square, this)

    @Suppress("TestFunctionName")
    @Composable
    private fun TestChessBoard(game: GameSession) {
        val sessionManager = LocalGameSession.current

        LaunchedEffect(Unit) {
            sessionManager.updateSession(game)
        }

        ChessTheme {
            Surface {
                Box(Modifier.fillMaxSize()) {
                    val boardViewModel =
                        ChessBoardViewModel(TestBoardSounds(), TestGameSessionRepository())
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
