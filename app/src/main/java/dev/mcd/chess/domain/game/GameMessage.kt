package dev.mcd.chess.domain.game

import dev.mcd.chess.domain.api.MoveHistory
import dev.mcd.chess.domain.api.SessionInfo

sealed interface GameMessage {

    data class SessionInfoMessage(
        val sessionInfo: SessionInfo,
    ) : GameMessage

    data class MoveHistoryMessage(
        val moveHistory: MoveHistory,
    ) : GameMessage

    data class MoveMessage(
        val move: String,
    ) : GameMessage

    object ErrorNotUsersMove : GameMessage
    object ErrorGameTerminated : GameMessage
}
