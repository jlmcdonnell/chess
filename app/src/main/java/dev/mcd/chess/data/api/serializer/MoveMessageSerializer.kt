package dev.mcd.chess.data.api.serializer

import kotlinx.serialization.Serializable

@Serializable
data class MoveMessageSerializer(
    val move: String,
    val count: Int,
)
