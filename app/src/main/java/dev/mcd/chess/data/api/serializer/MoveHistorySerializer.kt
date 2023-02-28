package dev.mcd.chess.data.api.serializer

import dev.mcd.chess.domain.api.MoveHistory
import kotlinx.serialization.Serializable

@Serializable
data class MoveHistorySerializer(
    val fen: String,
    val moveList: List<String>,
)

fun MoveHistorySerializer.domain() = MoveHistory(
    fen = fen,
    moveList = moveList,
)
