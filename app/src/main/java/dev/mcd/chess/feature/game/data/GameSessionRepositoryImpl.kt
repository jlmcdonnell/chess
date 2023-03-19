package dev.mcd.chess.feature.game.data

import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.feature.game.domain.GameSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GameSessionRepositoryImpl @Inject constructor() : GameSessionRepository {

    private val activeSession = MutableStateFlow<GameSession?>(null)

    override suspend fun activeGame(): StateFlow<GameSession?> {
        return activeSession
    }

    override suspend fun updateActiveGame(game: GameSession?) {
        activeSession.value = game
    }
}
