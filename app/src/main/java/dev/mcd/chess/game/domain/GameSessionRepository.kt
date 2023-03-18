package dev.mcd.chess.game.domain

import kotlinx.coroutines.flow.StateFlow

interface GameSessionRepository {
    suspend fun activeGame(): StateFlow<ClientGameSession?>
    suspend fun updateActiveGame(game: ClientGameSession?)
}
