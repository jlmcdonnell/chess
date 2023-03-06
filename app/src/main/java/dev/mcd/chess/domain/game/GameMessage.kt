package dev.mcd.chess.domain.game

import dev.mcd.chess.domain.game.online.GameSession

sealed interface GameMessage {

    data class GameState(
        val session: GameSession,
    ) : GameMessage

    data class MoveMessage(
        val move: String,
        val count: Int,
    ) : GameMessage

    object ErrorNotUsersMove : GameMessage
    object ErrorGameTerminated : GameMessage
    object ErrorInvalidMove : GameMessage
}
