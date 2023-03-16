package dev.mcd.chess.api.serializer

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.pgn.PgnIterator
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.game.online.GameSession
import kotlinx.serialization.Serializable
import java.util.Base64

@Serializable
internal data class GameStateMessageSerializer(
    val id: GameId,
    val pgn: String,
)

internal fun GameStateMessageSerializer.domain(): GameSession {
    val pgnDecoded = Base64.getDecoder().decode(pgn).decodeToString()
    val game = PgnIterator(pgnDecoded.lines().iterator()).first()
    game.board = Board()
    if (game.fen != null) {
        game.board.loadFromFen(game.fen)
    }
    game.gotoLast()
    return GameSession(
        id = id,
        game = game,
    )
}
