package dev.mcd.chess.domain.model

import com.github.bhlangonijr.chesslib.Side

data class TerminationReason(
    val sideMated: Side?,
    val draw: Boolean,
    val resignation: Side?,
)
