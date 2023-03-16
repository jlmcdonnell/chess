package dev.mcd.chess.api.serializer

import com.github.bhlangonijr.chesslib.Square
import org.junit.Assert
import org.junit.Test
import java.util.Base64

internal class InGameStateMessageSerializerTest {

    private val newGamePGN = """
     [Event "null"]
     [Site "null"]
     [Date "2023-03-05T17:47:03.747535Z"]
     [Round "0"]
     [White "MercyVirtual"]
     [Black "DisagreeFrown"]
     [Result "*"]
     [PlyCount "null"]
     [TimeControl "-"]
     
     1. e4 e5 2. Nf3 Nf6 *
    """.trimIndent()

    private val endgamePGN = """
        [Event "null"]
        [Site "null"]
        [Date "2023-03-08T12:40:04.385039Z"]
        [Round "0"]
        [White "p1"]
        [Black "p2"]
        [Result "*"]
        [PlyCount "null"]
        [TimeControl "-"]
        [FEN "3k4/8/3K3R/8/8/8/8/8 w - - 0 1"]

        *
    """.trimIndent()

    @Test
    fun newGameFEN() {
        val pgn64 = Base64.getEncoder().encodeToString(newGamePGN.toByteArray())
        val content = "{\"id\":\"id\",\"pgn\":\"$pgn64\"}".trimIndent()
        val state = GameMessageSerializer(MessageType.GameState, content).asGameState()
        Assert.assertEquals(Square.F6, state.session.game.board.backup.last.move.to)

        val fen = "rnbqkb1r/pppp1ppp/5n2/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3"
        Assert.assertEquals(fen, state.session.game.board.fen)
    }

    @Test
    fun endGameFEN() {
        val pgn64 = Base64.getEncoder().encodeToString(endgamePGN.toByteArray())
        val content = "{\"id\":\"id\",\"pgn\":\"$pgn64\"}".trimIndent()
        val state = GameMessageSerializer(MessageType.GameState, content).asGameState()

        val fen = "3k4/8/3K3R/8/8/8/8/8 w - - 0 1"
        Assert.assertEquals(fen, state.session.game.board.fen)
    }

}
