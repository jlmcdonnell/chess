@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package dev.mcd.chess.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.R
import dev.mcd.chess.ui.game.board.LocalGameSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

@Composable
fun CapturedPieces(side: Side) {
    val sessionManager = LocalGameSession.current
    val game by sessionManager.sessionUpdates.collectAsState()
    var capturedPieces by remember { mutableStateOf<List<Piece>>(emptyList()) }

    LaunchedEffect(Unit) {
        sessionManager.sessionUpdates
            .filterNotNull()
            .flatMapLatest { it.pieceUpdates }
            .collectLatest {
                capturedPieces = game?.board?.backup?.map { backup ->
                    backup.capturedPiece
                } ?: emptyList()
            }
    }
    Row(
        modifier = Modifier
            .height(16.dp)
            .padding(horizontal = 8.dp),
    ) {
        capturedPieces
            .filter { piece -> piece.pieceSide == side }
            .sortedBy { piece -> piece.pieceType.ordinal }
            .forEachIndexed { i, piece ->
                val resource = when (piece.pieceSide!!) {
                    Side.WHITE -> {
                        when (piece.pieceType!!) {
                            PieceType.PAWN -> R.drawable.captured_wp
                            PieceType.KNIGHT -> R.drawable.captured_wn
                            PieceType.BISHOP -> R.drawable.captured_wb
                            PieceType.ROOK -> R.drawable.captured_wr
                            PieceType.QUEEN -> R.drawable.captured_wq
                            else -> null
                        }
                    }
                    Side.BLACK -> {
                        when (piece.pieceType) {
                            PieceType.PAWN -> R.drawable.captured_bp
                            PieceType.KNIGHT -> R.drawable.captured_bn
                            PieceType.BISHOP -> R.drawable.captured_bb
                            PieceType.ROOK -> R.drawable.captured_br
                            PieceType.QUEEN -> R.drawable.captured_bq
                            else -> null
                        }
                    }
                }
                resource?.let {
                    Image(
                        modifier = Modifier
                            .fillMaxHeight(1f)
                            .offset(x = -(4 * i).dp),
                        contentScale = ContentScale.FillHeight,
                        painter = painterResource(id = resource),
                        contentDescription = null,
                    )
                }
            }
    }
}
