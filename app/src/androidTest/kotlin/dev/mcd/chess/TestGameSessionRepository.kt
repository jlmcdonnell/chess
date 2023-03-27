package dev.mcd.chess

import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.feature.game.domain.GameSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TestGameSessionRepository : GameSessionRepository {
    override suspend fun activeGame(): StateFlow<GameSession?> = MutableStateFlow(null)

    override suspend fun updateActiveGame(game: GameSession?) = Unit
}
