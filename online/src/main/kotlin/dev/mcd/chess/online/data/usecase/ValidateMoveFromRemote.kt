package dev.mcd.chess.online.data.usecase

import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.common.game.MoveString

object ValidateMoveFromRemote {
    enum class Result {
        InSync,
        SyncRequiredApplyMove,
    }

    operator fun invoke(move: MoveString, moveCount: Int, localSession: GameSession): Result {
        val lastMove = localSession.lastMove()?.move?.move.toString()
        val localMoveCount = localSession.moveCount
        return if (move.value == lastMove && moveCount == localMoveCount) {
            Result.InSync
        } else {
            Result.SyncRequiredApplyMove
        }
    }
}
