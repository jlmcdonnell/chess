package dev.mcd.chess.domain.api

import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.domain.game.BoardState
import dev.mcd.chess.domain.game.SessionId
import dev.mcd.chess.domain.game.State
import dev.mcd.chess.domain.player.UserId

data class SessionInfo(
    val sessionId: SessionId,
    val whiteUserId: UserId,
    val blackUserId: UserId,
    val state: State,
    val board: BoardState,
)

fun SessionInfo.sideForUser(userId: UserId) = if (whiteUserId == userId) Side.WHITE else Side.BLACK

fun SessionInfo.opponent(userId: UserId) = if (whiteUserId == userId) blackUserId else whiteUserId
