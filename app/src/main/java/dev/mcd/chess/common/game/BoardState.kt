package dev.mcd.chess.common.game

import com.github.bhlangonijr.chesslib.Side

data class BoardState(
    val fen: String,
    val lastMoveSide: Side?,
    val lastMoveSan: String?,
    val moveCount: Int,
)
