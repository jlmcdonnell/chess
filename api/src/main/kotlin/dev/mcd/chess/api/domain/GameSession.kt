package dev.mcd.chess.api.domain

import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.game.Game
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.player.UserId

data class GameSession(
    val id: GameId,
    val game: Game,
)


fun GameSession.opponent(id: UserId): UserId {
    return if (game.whitePlayer.id == id) game.blackPlayer.id else game.whitePlayer.id
}

fun GameSession.sideForUser(id: UserId): Side {
    return if (game.whitePlayer.id == id) Side.WHITE else Side.BLACK
}
