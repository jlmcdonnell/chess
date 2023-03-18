package dev.mcd.chess.online.domain.entity

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
