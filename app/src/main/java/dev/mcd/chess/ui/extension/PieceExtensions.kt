package dev.mcd.chess.ui.extension

import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.R

fun Piece.drawableResource(): Int {
    val isWhite = pieceSide == Side.WHITE
    return when (pieceType!!) {
        PieceType.BISHOP -> if (isWhite) R.drawable.wb else R.drawable.bb
        PieceType.KING -> if (isWhite) R.drawable.wk else R.drawable.bk
        PieceType.KNIGHT -> if (isWhite) R.drawable.wn else R.drawable.bn
        PieceType.PAWN -> if (isWhite) R.drawable.wp else R.drawable.bp
        PieceType.QUEEN -> if (isWhite) R.drawable.wq else R.drawable.bq
        PieceType.ROOK -> if (isWhite) R.drawable.wr else R.drawable.br
        PieceType.NONE -> throw Error("Unknown piece")
    }
}
