package dev.mcd.chess.ui.game.board

import app.cash.turbine.test
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Constants
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.common.player.HumanPlayer
import dev.mcd.chess.common.player.PlayerImage
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class GameSessionManagerTest : StringSpec(
    {
        "captures" {
            val player = HumanPlayer("", PlayerImage.Default, 0)
            val manager = GameSessionManager()

            val board1 = Board()
            board1.loadFromFen(Constants.startStandardFENPosition)

            val board2 = Board()
            board2.loadFromFen(Constants.startStandardFENPosition)

            val session1 = GameSession("1", player, player, Side.WHITE)
            val session2 = GameSession("2", player, player, Side.WHITE)

            session1.setBoard(board1)
            session2.setBoard(board2)

            manager.updateSession(session1)

            session1.captures() shouldBe emptyList()

            manager.captures().test {
                "e4 e5 Qh5 g6 Qe5 Be7 Qh8".split(" ").onEach {
                    session1.move(it)
                }
                awaitItem() shouldBe emptyList()
                awaitItem() shouldBe listOf(Piece.BLACK_PAWN)
                awaitItem() shouldBe listOf(Piece.BLACK_PAWN, Piece.BLACK_ROOK)

                manager.updateSession(session2)

                awaitItem() shouldBe emptyList()
            }
        }
    },
)
