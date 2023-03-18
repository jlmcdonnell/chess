package dev.mcd.chess.feature.game.domain

import dev.mcd.chess.common.game.ClientGameSession
import kotlinx.coroutines.flow.StateFlow

interface GameSessionRepository {
    suspend fun activeGame(): StateFlow<ClientGameSession?>
    suspend fun updateActiveGame(game: ClientGameSession?)
}
