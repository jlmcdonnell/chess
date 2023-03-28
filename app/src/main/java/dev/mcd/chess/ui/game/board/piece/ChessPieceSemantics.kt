package dev.mcd.chess.ui.game.board.piece

import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.Square

val PieceSquareKey = SemanticsPropertyKey<SquarePieceTag>("PieceSquareKey")
var SemanticsPropertyReceiver.pieceSquare by PieceSquareKey

data class SquarePieceTag(val square: Square, val piece: Piece)
