package dev.mcd.chess.domain.game

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface GameSessionRepository {
    suspend fun activeGame(): StateFlow<LocalGameSession?>
    suspend fun updateActiveGame(game: LocalGameSession?)
}

class GameSessionRepositoryImpl @Inject constructor() : GameSessionRepository {

    private val activeSession = MutableStateFlow<LocalGameSession?>(null)

    override suspend fun activeGame(): StateFlow<LocalGameSession?> {
        return activeSession
    }

    override suspend fun updateActiveGame(game: LocalGameSession?) {
        activeSession.value = game
    }
}
