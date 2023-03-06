package dev.mcd.chess.domain.game.online

import dev.mcd.chess.domain.game.GameId
import dev.mcd.chess.domain.game.TerminationReason
import kotlinx.coroutines.flow.Flow

interface JoinOnlineGame {
    sealed interface Event {
        data class Termination(val reason: TerminationReason) : Event
        data class FatalError(val message: String) : Event
    }
    suspend operator fun invoke(id: GameId): Flow<Event>
}
