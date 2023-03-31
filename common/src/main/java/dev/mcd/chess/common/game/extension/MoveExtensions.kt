package dev.mcd.chess.common.game.extension

import com.github.bhlangonijr.chesslib.MoveBackup
import com.github.bhlangonijr.chesslib.Square

fun Square.relevantToMove(move: MoveBackup): Boolean {
    return this in listOf(
        move.move.from,
        move.move.to,
        move.rookCastleMove?.from,
        move.rookCastleMove?.to,
        move.enPassantTarget,
    )
}
