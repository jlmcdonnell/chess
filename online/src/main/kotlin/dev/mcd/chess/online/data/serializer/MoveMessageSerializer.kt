package dev.mcd.chess.online.data.serializer

import kotlinx.serialization.Serializable

@Serializable
internal data class MoveMessageSerializer(
    val move: String,
    val count: Int,
)
