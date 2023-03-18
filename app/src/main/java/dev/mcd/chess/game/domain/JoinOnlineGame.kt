package dev.mcd.chess.game.domain

import dev.mcd.chess.common.game.GameId
import kotlinx.coroutines.flow.Flow

interface JoinOnlineGame {
    sealed interface Event {
        data class Termination(val reason: TerminationReason) : Event
        data class FatalError(val message: String) : Event
    }
    suspend operator fun invoke(id: GameId): Flow<Event>
}
