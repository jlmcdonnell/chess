package dev.mcd.chess

import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.feature.game.domain.BoardSounds

class TestBoardSounds : BoardSounds {
    override suspend fun notify() = Unit

    override suspend fun awaitMoves(session: GameSession) = Unit
}
