package dev.mcd.chess.feature.puzzle.data.usecase

import com.github.bhlangonijr.chesslib.Board
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.common.game.MoveResult
import dev.mcd.chess.common.player.HumanPlayer
import dev.mcd.chess.common.player.PuzzleOpponent
import dev.mcd.chess.feature.common.domain.Translations
import dev.mcd.chess.feature.game.domain.GameSessionRepository
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession.DelaySettings
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession.PuzzleInput
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession.PuzzleOutput
import dev.mcd.chess.online.domain.entity.Puzzle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.util.Stack
import java.util.UUID
import javax.inject.Inject

class CreatePuzzleSessionImpl @Inject constructor(
    private val gameSessionRepository: GameSessionRepository,
    private val translations: Translations,
) : CreatePuzzleSession {

    override suspend operator fun invoke(puzzle: Puzzle, settings: DelaySettings): Pair<PuzzleInput, Flow<PuzzleOutput>> {
        val moves = Stack<String>().apply {
            addAll(puzzle.moves.reversed())
        }
        val session = initSession(puzzle)
        val puzzleInputChannel = PuzzleInputChannel(session)

        val out = channelFlow {
            send(PuzzleOutput.Started(session))

            delay(settings.beforePuzzleStartDelay)
            send(moveForOpponent(session, puzzle, moves))

            try {
                for (move in puzzleInputChannel.moves()) {
                    send(moveForPlayer(session, puzzle, moves, move, settings))
                }
            } finally {
                close()
            }
        }

        return puzzleInputChannel to out
    }

    private suspend fun moveForPlayer(
        session: GameSession,
        puzzle: Puzzle,
        moves: Stack<String>,
        move: String,
        settings: DelaySettings,
    ): PuzzleOutput {
        if (moves.isEmpty()) return PuzzleOutput.NoMovesLeft
        if (!session.isSelfTurn()) return PuzzleOutput.NotUserTurn

        val nextMove = moves.peek()

        if (session.move(move) != MoveResult.Moved) {
            return PuzzleOutput.ErrorMoveInvalid(puzzle.puzzleId)
        }
        delay(settings.afterPlayerMoveDelay)
        return if (move == nextMove) {
            moves.pop()
            if (moves.isEmpty()) {
                PuzzleOutput.Completed
            } else {
                moveForOpponent(session, puzzle, moves)
            }
        } else {
            PuzzleOutput.Failed
        }
    }

    private suspend fun moveForOpponent(session: GameSession, puzzle: Puzzle, moves: Stack<String>): PuzzleOutput {
        if (moves.isEmpty()) return PuzzleOutput.NoMovesLeft

        val nextMove = moves.peek()
        if (session.move(nextMove) != MoveResult.Moved) {
            return PuzzleOutput.ErrorMoveInvalid(puzzle.puzzleId)
        }
        return if (moves.isEmpty()) {
            PuzzleOutput.Completed
        } else {
            moves.pop()
            PuzzleOutput.MoveCorrect
        }
    }

    private suspend fun initSession(puzzle: Puzzle): GameSession {
        val board = Board().apply {
            clear()
            loadFromFen(puzzle.fen)
        }
        return GameSession(
            id = "${puzzle.puzzleId}:${UUID.randomUUID()}",
            self = HumanPlayer(
                name = translations.playerYou,
            ),
            opponent = PuzzleOpponent(
                name = translations.playerPuzzle,
            ),
            selfSide = board.sideToMove.flip(),
        ).also {
            it.setBoard(board)
            gameSessionRepository.updateActiveGame(it)
        }
    }
}
