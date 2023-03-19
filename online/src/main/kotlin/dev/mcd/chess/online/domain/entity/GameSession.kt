package dev.mcd.chess.online.domain.entity

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.game.GameResult
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.player.UserId

internal data class GameSession(
    val id: GameId,
    val whitePlayer: UserId,
    val blackPlayer: UserId,
    val board: Board,
    val result: GameResult,
)
