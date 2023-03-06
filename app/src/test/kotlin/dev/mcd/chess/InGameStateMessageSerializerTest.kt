package dev.mcd.chess

import com.github.bhlangonijr.chesslib.Square
import dev.mcd.chess.data.api.serializer.GameMessageSerializer
import dev.mcd.chess.data.api.serializer.MessageType
import dev.mcd.chess.data.api.serializer.asGameState
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Base64

internal class InGameStateMessageSerializerTest {

    private val testPGN = """
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

    @Test
    fun serializer() {
        val pgn64 = Base64.getEncoder().encodeToString(testPGN.toByteArray())
        val content = "{\"id\":\"id\",\"pgn\":\"$pgn64\"}".trimIndent()
        val state = GameMessageSerializer(MessageType.GameState, content).asGameState()
        assertEquals(Square.F6, state.session.game.board.backup.last.move.to)

        val fen = "rnbqkb1r/pppp1ppp/5n2/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3"
        assertEquals(fen, state.session.game.board.fen)
    }

}
