package dev.mcd.chess.ui.extension

import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.R

fun Piece.drawableResource(): Int {
    val isWhite = pieceSide == Side.WHITE
    return when (pieceType!!) {
        PieceType.BISHOP -> if (isWhite) R.drawable.piece_wb else R.drawable.piece_bb
        PieceType.KING -> if (isWhite) R.drawable.piece_wk else R.drawable.piece_bk
        PieceType.KNIGHT -> if (isWhite) R.drawable.piece_wn else R.drawable.piece_bn
        PieceType.PAWN -> if (isWhite) R.drawable.piece_wp else R.drawable.piece_bp
        PieceType.QUEEN -> if (isWhite) R.drawable.piece_wq else R.drawable.piece_bq
        PieceType.ROOK -> if (isWhite) R.drawable.piece_wr else R.drawable.piece_br
        PieceType.NONE -> throw Error("Unknown piece")
    }
}
