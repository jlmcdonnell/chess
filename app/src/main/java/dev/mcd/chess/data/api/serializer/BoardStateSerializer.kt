package dev.mcd.chess.data.api.serializer

import androidx.annotation.Keep
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.domain.game.BoardState
import kotlinx.serialization.Serializable

@Serializable
@Keep
data class BoardStateSerializer(
    val fen: String,
    val lastMoveSide: String?,
    val lastMoveSan: String?,
    val moveCount: Int,
)

fun BoardStateSerializer.toBoardState() = BoardState(
    fen = fen,
    lastMoveSide = lastMoveSide?.let(Side::valueOf),
    lastMoveSan = lastMoveSan,
    moveCount = moveCount,
)
