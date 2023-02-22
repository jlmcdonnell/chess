package dev.mcd.chess.domain.game

sealed interface GameMessage {

    data class BoardStateMessage(
        val state: BoardState,
    ) : GameMessage

    object ErrorNotUsersMove : GameMessage
    object GameTermination : GameMessage
}