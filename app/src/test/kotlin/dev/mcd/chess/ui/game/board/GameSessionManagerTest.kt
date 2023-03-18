package dev.mcd.chess.ui.game.board

import app.cash.turbine.test
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Constants
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.game.domain.ClientGameSession
import dev.mcd.chess.common.player.HumanPlayer
import dev.mcd.chess.common.player.PlayerImage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GameSessionManagerTest {

    @Test
    fun captures() {
        runBlocking {
            val player = HumanPlayer("", PlayerImage.None, 0)
            val manager = GameSessionManager()

            val board1 = Board()
            board1.loadFromFen(Constants.startStandardFENPosition)

            val board2 = Board()
            board2.loadFromFen(Constants.startStandardFENPosition)

            val session1 = ClientGameSession("1", player, Side.WHITE, player)
            val session2 = ClientGameSession("2", player, Side.WHITE, player)

            session1.setBoard(board1)
            session2.setBoard(board2)

            manager.updateSession(session1)

            assertEquals(emptyList<Piece>(), session1.captures())

            manager.captures().test {
                "e4 e5 Qh5 g6 Qe5 Be7 Qh8".split(" ").onEach {
                    session1.move(it)
                }
                assertEquals(emptyList<Piece>(), awaitItem())
                assertEquals(listOf(Piece.BLACK_PAWN), awaitItem())
                assertEquals(listOf(Piece.BLACK_PAWN, Piece.BLACK_ROOK), awaitItem())

                manager.updateSession(session2)

                assertEquals(emptyList<Piece>(), awaitItem())
            }
        }
    }
}
