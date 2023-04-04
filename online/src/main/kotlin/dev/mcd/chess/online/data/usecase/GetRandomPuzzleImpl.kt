package dev.mcd.chess.online.data.usecase

import dev.mcd.chess.online.domain.ChessApi
import dev.mcd.chess.online.domain.entity.Puzzle
import dev.mcd.chess.online.domain.usecase.GetRandomPuzzle
import javax.inject.Inject

internal class GetRandomPuzzleImpl @Inject constructor(
    internal val chessApi: ChessApi,
) : GetRandomPuzzle {
    override suspend operator fun invoke(): Puzzle {
        return chessApi.getRandomPuzzle()
    }
}
