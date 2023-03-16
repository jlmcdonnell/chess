package dev.mcd.chess.common.game.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface GameSessionRepository {
    suspend fun activeGame(): StateFlow<ClientGameSession?>
    suspend fun updateActiveGame(game: ClientGameSession?)
}

class GameSessionRepositoryImpl @Inject constructor() : GameSessionRepository {

    private val activeSession = MutableStateFlow<ClientGameSession?>(null)

    override suspend fun activeGame(): StateFlow<ClientGameSession?> {
        return activeSession
    }

    override suspend fun updateActiveGame(game: ClientGameSession?) {
        activeSession.value = game
    }
}
