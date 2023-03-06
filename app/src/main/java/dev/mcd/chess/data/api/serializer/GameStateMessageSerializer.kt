package dev.mcd.chess.data.api.serializer

import androidx.annotation.Keep
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.pgn.PgnIterator
import dev.mcd.chess.domain.game.GameId
import dev.mcd.chess.domain.game.online.GameSession
import kotlinx.serialization.Serializable
import timber.log.Timber
import java.util.Base64

@Serializable
@Keep
data class GameStateMessageSerializer(
    val id: GameId,
    val pgn: String,
)

fun GameStateMessageSerializer.domain(): GameSession {
    val pgnDecoded = Base64.getDecoder().decode(pgn).decodeToString()
    Timber.d("PGN ============ \n $pgnDecoded")
    val game = PgnIterator(pgnDecoded.lines().iterator()).first()
    game.board = Board()
    game.gotoLast()
    return GameSession(
        id = id,
        game = game,
    )
}
