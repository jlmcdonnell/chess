package dev.mcd.chess.online.data.serializer

import dev.mcd.chess.online.domain.entity.Puzzle
import kotlinx.serialization.Serializable

@Serializable
data class PuzzleSerializer(
    val puzzleId: String,
    val fen: String,
    val moves: List<String>,
    val rating: Int,
    val themes: List<String>,
)

fun PuzzleSerializer.domain(): Puzzle {
    return Puzzle(
        puzzleId = puzzleId,
        fen = fen,
        moves = moves,
        rating = rating,
        themes = themes,
    )
}
