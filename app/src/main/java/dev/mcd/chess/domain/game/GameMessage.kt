package dev.mcd.chess.domain.game

import dev.mcd.chess.domain.api.SessionInfo

sealed interface GameMessage {

    data class SessionInfoMessage(
        val sessionInfo: SessionInfo,
    ) : GameMessage

    object ErrorNotUsersMove : GameMessage
}
