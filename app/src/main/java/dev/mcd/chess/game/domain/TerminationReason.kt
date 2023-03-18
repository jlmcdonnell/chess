package dev.mcd.chess.game.domain

import com.github.bhlangonijr.chesslib.Side

data class TerminationReason(
    val sideMated: Side? = null,
    val draw: Boolean = false,
    val resignation: Side? = null,
) {
    init {
        if (sideMated == null && !draw && resignation == null) throw Exception("Invalid termination reason")
    }
}
