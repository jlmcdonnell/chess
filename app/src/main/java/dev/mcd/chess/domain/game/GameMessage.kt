package dev.mcd.chess.domain.game

import com.github.bhlangonijr.chesslib.Side

sealed interface GameMessage {

    data class BoardState(
        val side: Side,
        val fen: String,
        val lastMoveSide: String?,
        val lastMoveSan: String?,
        val plyCount: Int,
        val moveCount: Int,
    ) : GameMessage

    object ErrorNotUsersMove : GameMessage
    object GameTermination : GameMessage
}