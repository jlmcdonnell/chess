package dev.mcd.chess.feature.game.domain

import dev.mcd.chess.common.game.GameSession
import kotlinx.coroutines.flow.StateFlow

interface GameSessionRepository {
    suspend fun activeGame(): StateFlow<GameSession?>
    suspend fun updateActiveGame(game: GameSession?)
}
