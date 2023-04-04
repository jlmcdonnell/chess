package dev.mcd.chess.feature.puzzle.data.usecase

import com.github.bhlangonijr.chesslib.Board
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.common.game.MoveResult
import dev.mcd.chess.common.player.HumanPlayer
import dev.mcd.chess.common.player.PuzzleOpponent
import dev.mcd.chess.feature.common.domain.Translations
import dev.mcd.chess.feature.game.domain.GameSessionRepository
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession.PuzzleInput
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession.PuzzleOutput
import dev.mcd.chess.online.domain.entity.Puzzle
import kotlinx.coroutines.channels.Channel
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

    override suspend operator fun invoke(puzzle: Puzzle): Pair<PuzzleInput, Flow<PuzzleOutput>> {
        val moves = Stack<String>().apply {
            addAll(puzzle.moves.reversed())
        }
        val session = initSession(puzzle)
        val channel = Channel<String>()
        println(moves)

        val input = object : PuzzleInput {
            override suspend fun close() {
                channel.close()
            }

            override suspend fun move(move: String) {
                channel.send(move)
            }

            override suspend fun retry() {
                session.undo(eraseHistory = true)
            }
        }

        val out = channelFlow {
            send(PuzzleOutput.Started(session))

            delay(1000)
            send(moveForOpponent(session, puzzle, moves))

            try {
                for (move in channel) {
                    send(moveForPlayer(session, puzzle, moves, move))
                }
            } finally {
                close()
            }
        }

        return input to out
    }

    private suspend fun moveForPlayer(session: GameSession, puzzle: Puzzle, moves: Stack<String>, move: String): PuzzleOutput {
        if (moves.isEmpty()) return PuzzleOutput.NoMovesLeft
        if (!session.isSelfTurn()) return PuzzleOutput.NotUserTurn

        val nextMove = moves.peek()

        if (session.move(move) != MoveResult.Moved) {
            return PuzzleOutput.ErrorMoveInvalid(puzzle.puzzleId)
        }
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
        delay(200)
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
