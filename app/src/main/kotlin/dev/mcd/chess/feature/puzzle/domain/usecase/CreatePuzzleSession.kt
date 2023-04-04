package dev.mcd.chess.feature.puzzle.domain.usecase

import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.online.domain.entity.Puzzle
import kotlinx.coroutines.flow.Flow

interface CreatePuzzleSession {
    sealed interface PuzzleOutput {
        data class Started(val session: GameSession) : PuzzleOutput
        object Completed : PuzzleOutput
        object Failed : PuzzleOutput
        object NotUserTurn : PuzzleOutput
        object NoMovesLeft : PuzzleOutput
        object MoveCorrect : PuzzleOutput
        data class ErrorMoveInvalid(val id: String) : PuzzleOutput
    }

    interface PuzzleInput {
        suspend fun move(move: String)
        suspend fun retry()
        suspend fun close()
    }

    suspend operator fun invoke(puzzle: Puzzle): Pair<PuzzleInput, Flow<PuzzleOutput>>
}
