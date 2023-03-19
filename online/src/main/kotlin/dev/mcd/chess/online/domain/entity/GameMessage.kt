package dev.mcd.chess.online.domain.entity

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.game.GameResult
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.player.UserId

sealed interface GameMessage {

    data class GameState(
        val id: GameId,
        val whitePlayer: UserId,
        val blackPlayer: UserId,
        val board: Board,
        val result: GameResult,
    ) : GameMessage

    data class MoveMessage(
        val move: String,
        val count: Int,
    ) : GameMessage

    object ErrorNotUsersMove : GameMessage

    object ErrorGameTerminated : GameMessage

    object ErrorInvalidMove : GameMessage
}
