package dev.mcd.chess.domain.game

import com.github.bhlangonijr.chesslib.Side

data class BoardState(
    val fen: String,
    val lastMoveSide: Side?,
    val lastMoveSan: String?,
    val moveCount: Int,
)
