package dev.mcd.chess.feature.game.data

import dev.mcd.chess.feature.game.domain.ClientGameSession
import dev.mcd.chess.feature.game.domain.GameSessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GameSessionRepositoryImpl @Inject constructor() : GameSessionRepository {

    private val activeSession = MutableStateFlow<ClientGameSession?>(null)

    override suspend fun activeGame(): StateFlow<ClientGameSession?> {
        return activeSession
    }

    override suspend fun updateActiveGame(game: ClientGameSession?) {
        activeSession.value = game
    }
}
