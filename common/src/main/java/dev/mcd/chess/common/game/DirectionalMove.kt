package dev.mcd.chess.common.game

import com.github.bhlangonijr.chesslib.MoveBackup

data class DirectionalMove(
    val move: MoveBackup,
    val undo: Boolean,
)
