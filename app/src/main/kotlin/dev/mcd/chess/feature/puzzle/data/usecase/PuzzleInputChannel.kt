package dev.mcd.chess.feature.puzzle.data.usecase

import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

class PuzzleInputChannel(
    private val session: GameSession,
) : CreatePuzzleSession.PuzzleInput {

    private val channel = Channel<String>()

    override fun move(move: String) {
        channel.trySend(move)
    }

    override suspend fun retry() {
        if (session.undoneMoves().isEmpty()) {
            session.undo(eraseHistory = true)
        } else {
            do {
                session.redo()
            } while (session.undoneMoves().isNotEmpty())
            session.undo(eraseHistory = true)
        }
    }

    override suspend fun close() {
        channel.close()
    }

    fun moves(): ReceiveChannel<String> = channel

}
