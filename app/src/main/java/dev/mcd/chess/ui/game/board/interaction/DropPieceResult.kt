package dev.mcd.chess.ui.game.board.interaction

import com.github.bhlangonijr.chesslib.Square

sealed interface DropPieceResult {
    object None : DropPieceResult
    object Promoting : DropPieceResult
    data class Moved(val to: Square) : DropPieceResult
}
