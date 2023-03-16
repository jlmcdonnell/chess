package dev.mcd.chess.api.serializer

import kotlinx.serialization.Serializable

@Serializable
internal data class MoveMessageSerializer(
    val move: String,
    val count: Int,
)
