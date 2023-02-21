package dev.mcd.chess.data.api

import kotlinx.serialization.Serializable

@Serializable
data class BoardStateSerializer(
    val side: String,
    val fen: String,
    val lastMoveSide: String?,
    val lastMoveSan: String?,
    val plyCount: Int,
    val moveCount: Int,
)
