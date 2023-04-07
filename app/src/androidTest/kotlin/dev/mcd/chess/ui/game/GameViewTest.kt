package dev.mcd.chess.ui.game

/* ktlint-disable no-wildcard-imports other-rule-id */

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Piece.*
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.Square.*
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.test.createGameSessionRule
import dev.mcd.chess.ui.LocalGameSession
import dev.mcd.chess.ui.compose.StableHolder
import dev.mcd.chess.ui.extension.topLeft
import dev.mcd.chess.ui.game.board.piece.PieceSquareKey
import dev.mcd.chess.ui.game.board.piece.SquarePieceTag
import dev.mcd.chess.ui.theme.ChessTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GameViewTest {

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val gameRule = createGameSessionRule()

    @Test
    fun piecesRespondToBoardMoves(): Unit = runBlocking {
        with(composeRule) {
            setupChessBoard()

            assertPiece(WHITE_PAWN on E2)
            assertPiece(BLACK_PAWN on E7)

            move("e2e4")
            move("e7e5")

            assertPiece(WHITE_PAWN on E4)
            assertPiece(BLACK_PAWN on E5)

            assertNoPiece(WHITE_PAWN on E2)
            assertNoPiece(BLACK_PAWN on E7)
        }
    }

    @Test
    fun pieceHighlighting(): Unit = runBlocking {
        with(composeRule) {
            setupChessBoard()

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
            setupChessBoard()

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
            setupChessBoard()

            move("e2e4")

            assertPiece(WHITE_PAWN on E4)

            undoMove()

            assertPiece(WHITE_PAWN on E2)
            assertNoPiece(WHITE_PAWN on E4)
        }
    }

    @Test
    fun undoMoveAfterUserInput(): Unit = runBlocking {
        with(composeRule) {
            setupChessBoard()

            dragPiece(WHITE_PAWN, E2, E4)
            assertPiece(WHITE_PAWN on E4)

            undoMove()

            assertPiece(WHITE_PAWN on E2)
            assertNoPiece(WHITE_PAWN on E4)
        }
    }

    @Test
    fun undoMoveAfterCapture(): Unit = runBlocking {
        with(composeRule) {
            setupChessBoard()

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
            setupChessBoard()

            dragPiece(WHITE_PAWN, "e2e4")
            move("e7e5")
            dragPiece(WHITE_QUEEN, "d1h5")
            move("g7g6")
            dragPiece(WHITE_QUEEN, "h5e5")
            move("f8e7")
            dragPiece(WHITE_QUEEN, "e5h8")

            undoMove()

            assertPiece(
                BLACK_ROOK on H8,
                WHITE_QUEEN on E5,
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
            setupChessBoard()

            val moves = "e2e4 e7e6 b1c3 b8c6 f1b5 a7a6 b5c6 b7c6 d1h5 d7d5 e4d5 c6d5 c3d5 d8d5 h5d5 e6d5".split(" ")

            moves.forEach { move(it) }

            repeat(moves.size) { undoMove() }

            assertPiece(
                BLACK_PAWN on B7,
                BLACK_PAWN on D7,
            )
        }
    }

    @Test
    fun promote(): Unit = runBlocking {
        with(composeRule) {
            val board = gameRule.board.apply {
                clear()
                sideToMove = Side.WHITE
                setPiece(BLACK_KING, A8)
                setPiece(WHITE_KING, E1)
                setPiece(WHITE_PAWN, E7)
            }
            gameRule.game.setBoard(board)

            setupChessBoard()

            dragPiece(WHITE_PAWN, "e7e8")
            onNodeWithContentDescription("e7e8n").performClick()
            mainClock.advanceTimeBy(300)
            assertPiece(WHITE_KNIGHT on E8)
            onNodeWithContentDescription("e7e8n").assertDoesNotExist()
        }
    }

    @Test
    fun cancelPromotion(): Unit = runBlocking {
        with(composeRule) {
            val board = gameRule.board.apply {
                clear()
                sideToMove = Side.WHITE
                setPiece(BLACK_KING, A8)
                setPiece(WHITE_KING, E1)
                setPiece(WHITE_PAWN, E7)
            }
            gameRule.game.setBoard(board)

            setupChessBoard()

            delay(2000)
            dragPiece(WHITE_PAWN, "e7e8")
            delay(2000)
            onNodeWithTag("promotion-selector").performTouchInput {
                down(Offset.Zero)
                up()
            }
            mainClock.advanceTimeBy(300)
            delay(2000)
            assertPiece(WHITE_PAWN on E7)
            onNodeWithTag("promotion-selector").assertDoesNotExist()
        }
    }

    @Test
    fun undoAfterPromotion(): Unit = runBlocking {
        with(composeRule) {
            val board = gameRule.board.apply {
                clear()
                sideToMove = Side.WHITE
                setPiece(BLACK_KING, E8)
                setPiece(WHITE_KING, E1)
                setPiece(WHITE_PAWN, A7)
            }
            gameRule.game.setBoard(board)

            setupChessBoard()

            move("a7a8q")
            undoMove()

            assertPiece(WHITE_PAWN on A7)
        }
    }

    @Test
    fun undoAfterEnPassant(): Unit = runBlocking {
        with(composeRule) {
            val board = gameRule.board.apply {
                clear()
                sideToMove = Side.WHITE
                setPiece(BLACK_KING, E8)
                setPiece(WHITE_KING, E1)
                setPiece(WHITE_PAWN, E2)
                setPiece(BLACK_PAWN, D4)
            }
            gameRule.game.setBoard(board)

            setupChessBoard()

            move("e2e4")
            move("d4e3")
            undoMove()

            assertPiece(WHITE_PAWN on E4)
            assertPiece(BLACK_PAWN on D4)
            assertNoPiece(BLACK_PAWN on E3)
        }
    }

    @Test
    fun undoAfterCastle(): Unit = runBlocking {
        with(composeRule) {
            val board = gameRule.board.apply {
                clear()
                sideToMove = Side.WHITE
                setPiece(WHITE_KING, E1)
                setPiece(WHITE_ROOK, H1)
                setPiece(WHITE_ROOK, A1)
                setPiece(BLACK_KING, E8)
                setPiece(BLACK_ROOK, H8)
                setPiece(BLACK_ROOK, A8)
            }
            gameRule.game.setBoard(board)

            setupChessBoard()

            move("O-O-O")
            undoMove()
            assertPiece(WHITE_ROOK on A1)

            move("O-O")
            undoMove()
            assertPiece(WHITE_ROOK on H1)

            gameRule.board.sideToMove = Side.BLACK
            move("O-O-O")
            undoMove()
            assertPiece(BLACK_ROOK on A8)

            move("O-O")
            undoMove()
            assertPiece(BLACK_ROOK on H8)
        }
    }

    @Test
    fun moveAfterTermination() = runBlocking {
        with(composeRule) {
            setupChessBoard()
            gameRule.game.resign()
            dragPiece(WHITE_PAWN, "e2e4")
            assertNoPiece(WHITE_PAWN on E4)
        }
    }

    @Test
    fun flipBoard() = runBlocking {
        with(composeRule) {
            setupChessBoard()

            val e2Layout = onPiece(WHITE_PAWN on E2).getBoundsInRoot()
            val e7Layout = onPiece(BLACK_PAWN on D7).getBoundsInRoot()

            flipBoard()

            val e2LayoutFlipped = onPiece(WHITE_PAWN on E2).getBoundsInRoot()
            val e7LayoutFlipped = onPiece(BLACK_PAWN on D7).getBoundsInRoot()

            assertEquals(e7Layout, e2LayoutFlipped)
            assertEquals(e2Layout, e7LayoutFlipped)
        }
    }

    @Test
    fun flipBoardAfterMoving() = runBlocking {
        with(composeRule) {
            setupChessBoard()

            move("e2e3")
            move("d7d6")
            move("e3e4")
            move("d6d5")

            val e4Layout = onPiece(WHITE_PAWN on E4).getBoundsInRoot()
            val d5Layout = onPiece(BLACK_PAWN on D5).getBoundsInRoot()

            flipBoard()

            val e4LayoutFlipped = onPiece(WHITE_PAWN on E4).getBoundsInRoot()
            val d5LayoutFlipped = onPiece(BLACK_PAWN on D5).getBoundsInRoot()

            assertEquals(d5Layout, e4LayoutFlipped)
            assertEquals(e4Layout, d5LayoutFlipped)
        }
    }

    @Test
    fun flipAndUnflipAfterMoving() = runBlocking {

        with(composeRule) {
            val board = Board().apply {
                setPiece(WHITE_KING, E1)
                setPiece(BLACK_KING, E8)
                setPiece(WHITE_PAWN, E2)
                setPiece(BLACK_PAWN, D7)
            }
            gameRule.game.setBoard(board)
            setupChessBoard()

            move("e2e4")
            move("d7d5")

            val e4Layout = onPiece(WHITE_PAWN on E4).getBoundsInRoot()
            val d5Layout = onPiece(BLACK_PAWN on D5).getBoundsInRoot()

            flipBoard()
            flipBoard()

            val e4LayoutUnflipped = onPiece(WHITE_PAWN on E4).getBoundsInRoot()
            val d5LayoutUnflipped = onPiece(BLACK_PAWN on D5).getBoundsInRoot()

            assertEquals(d5Layout, d5LayoutUnflipped)
            assertEquals(e4Layout, e4LayoutUnflipped)
        }
    }

    @Test
    fun flipBoardAndUndo() = runBlocking {
        with(composeRule) {
            val board = gameRule.board.apply {
                clear()
                setPiece(WHITE_KING, E1)
                setPiece(BLACK_KING, E8)
                setPiece(WHITE_PAWN, E2)
            }
            gameRule.game.setBoard(board)
            setupChessBoard()

            move("e2e4")

            flipBoard()
            undoMove()

            assertPiece(WHITE_PAWN on E2)
        }
    }

    @Test
    fun pieceBeingCapturedWithSameLastMoveAsCapturingPieceGetsCaptured() = runBlocking {
        with(composeRule) {
            val board = Board().apply {
                clear()
                setPiece(BLACK_KING, E8)
                setPiece(WHITE_KING, E4)
                setPiece(BLACK_PAWN, E3)
                sideToMove = Side.BLACK
            }
            gameRule.game.setBoard(board)
            setupChessBoard()

            move("e2")
            move("Ke3")
            move("Ke7")
            move("Ke2")

            assertNoPiece(BLACK_PAWN on E2)

            repeat(times = 4) {
                undoMove()
            }

            assertPiece(BLACK_KING on E8)
            assertPiece(WHITE_KING on E4)
            assertPiece(BLACK_PAWN on E3)
        }
    }

    context(ComposeContentTestRule)
    private fun setupChessBoard(game: GameSession = gameRule.game) {
        mainClock.autoAdvance = false
        setContent {
            TestChessBoard(game)
        }
        mainClock.advanceTimeBy(1000)
    }

    context(ComposeTestRule)
    private suspend fun move(move: String) {
        gameRule.game.move(move)
        mainClock.advanceTimeBy(200)
    }

    context(ComposeTestRule)
    private fun dragPiece(piece: Piece, move: String) {
        dragPiece(
            piece,
            Square.valueOf(move.substring(0, 2).uppercase()),
            Square.valueOf(move.substring(2, 4).uppercase()),
        )
    }

    context(ComposeTestRule)
    private fun dragPiece(piece: Piece, from: Square, to: Square) {
        onPiece(piece on from)
            .performTouchInput {
                down(from.position())
                moveTo(to.position())
                up()
            }
        mainClock.advanceTimeBy(500)
    }

    context(ComposeTestRule)
    private fun undoMove() {
        onNode(hasContentDescription("Undo move")).performClick()
        mainClock.advanceTimeBy(500)
    }

    context(ComposeTestRule)
    private fun flipBoard() {
        onNode(hasContentDescription("Flip board")).performClick()
        mainClock.advanceTimeBy(500)
    }

    context(ComposeTestRule)
    private fun assertPiece(vararg pieceSquare: SquarePieceTag) {
        pieceSquare.map { onPiece(it) }.onEach(SemanticsNodeInteraction::assertExists)
    }


    context(ComposeTestRule)
    private fun assertNoPiece(vararg pieceSquare: SquarePieceTag) {
        pieceSquare.forEach {
            onPiece(it).assertDoesNotExist()
        }
    }

    context(ComposeTestRule)
    private fun onPiece(tag: SquarePieceTag): SemanticsNodeInteraction {
        return onNode(SemanticsMatcher.expectValue(PieceSquareKey, tag))
    }

    private infix fun Piece.on(square: Square): SquarePieceTag = SquarePieceTag(square, this)

    private fun Square.position(): Offset {
        val squareSize = composeRule.onNodeWithContentDescription("Board")
            .fetchSemanticsNode()
            .layoutInfo
            .width / 8f

        val (x, y) = topLeft(isWhite = true, squareSize)
        return Offset(x + squareSize / 2f, y + squareSize / 2f)
    }
}

@Suppress("TestFunctionName")
@Composable
private fun TestChessBoard(game: GameSession) {
    val sessionManager = LocalGameSession.current
    var size by remember { mutableStateOf<Float?>(null) }

    LaunchedEffect(Unit) {
        sessionManager.updateSession(game)
    }

    ChessTheme {
        Surface(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        size = it.size.width.toFloat()
                    },
            ) {
                size?.let {
                    GameView(
                        gameHolder = StableHolder(game),
                        onMove = {
                            runBlocking { game.move(it.toString()) }
                        },
                        onResign = {},
                        sounds = {},
                        boardWidth = { size!! },
                    )
                }
            }
        }
    }
}
