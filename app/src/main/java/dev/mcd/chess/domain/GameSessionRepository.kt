package dev.mcd.chess.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface GameSessionRepository {
    suspend fun activeGame(): StateFlow<GameSession?>
    suspend fun updateActiveGame(game: GameSession?)
}

class GameSessionRepositoryImpl @Inject constructor() : GameSessionRepository {

    private val activeSession = MutableStateFlow<GameSession?>(null)

    override suspend fun activeGame(): StateFlow<GameSession?> {
        return activeSession
    }

    override suspend fun updateActiveGame(game: GameSession?) {
        activeSession.value = game
    }
}
