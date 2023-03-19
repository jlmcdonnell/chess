package dev.mcd.chess.online.data.serializer

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.pgn.PgnIterator
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.online.domain.entity.GameMessage
import kotlinx.serialization.Serializable
import java.util.Base64

@Serializable
internal data class GameStateMessageSerializer(
    val id: GameId,
    val pgn: String,
)

internal fun GameStateMessageSerializer.domain(): GameMessage.GameState {
    val pgnDecoded = Base64.getDecoder().decode(pgn).decodeToString()
    val game = PgnIterator(pgnDecoded.lines().iterator()).first()
    game.board = Board()
    if (game.fen != null) {
        game.board.loadFromFen(game.fen)
    }
    game.gotoLast()
    return GameMessage.GameState(
        id = id,
        board = game.board,
        whitePlayer = game.whitePlayer.id,
        blackPlayer = game.blackPlayer.id,
        result = game.result,
    )
}
