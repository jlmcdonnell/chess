package dev.mcd.chess.online.domain.entity

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.game.GameResult
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.player.UserId

data class GameSession(
    val id: GameId,
    val whitePlayer: UserId,
    val blackPlayer: UserId,
    val board: Board,
    val result: GameResult,
)


fun GameSession.opponent(id: UserId): UserId {
    return if (whitePlayer == id) blackPlayer else whitePlayer
}

fun GameSession.sideForUser(id: UserId): Side {
    return if (whitePlayer == id) Side.WHITE else Side.BLACK
}
