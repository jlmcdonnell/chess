package dev.mcd.chess.common.game.extension

import com.github.bhlangonijr.chesslib.Square
import dev.mcd.chess.common.game.DirectionalMove
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

fun Flow<DirectionalMove>.relevantToSquare(square: Square): Flow<DirectionalMove> {
    return filter { (move, _) ->
        square in listOf(
            move.move.from,
            move.move.to,
            move.rookCastleMove?.from,
            move.rookCastleMove?.to,
            move.enPassantTarget
        )
    }
}
