package dev.mcd.chess.online.domain.usecase

import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.game.TerminationReason
import kotlinx.coroutines.flow.Flow

interface JoinOnlineGame {
    sealed interface Event {
        data class NewSession(val session: GameSession) : Event
        data class Termination(val reason: TerminationReason) : Event
        data class FatalError(val message: String) : Event
    }
    suspend operator fun invoke(id: GameId): Flow<Event>
}
