package dev.mcd.chess.ui.game.board.interaction

import com.github.bhlangonijr.chesslib.Square
import com.github.bhlangonijr.chesslib.move.Move

sealed interface DropPieceResult {
    object None : DropPieceResult
    data class SelectPromotion(val promotions: List<Move>) : DropPieceResult
    data class Moved(val from: Square, val to: Square) : DropPieceResult
}
