package dev.mcd.chess.feature.game.domain.usecase

import dev.mcd.chess.engine.lc0.FenParam
import dev.mcd.chess.feature.engine.BotEngineProxy
import dev.mcd.chess.feature.game.domain.GameSessionRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.math.max

class MoveForBotImpl @Inject constructor(
    private val gameSessionRepository: GameSessionRepository,
    private val engine: BotEngineProxy,
) : MoveForBot {

    override suspend fun invoke() {
        val game = gameSessionRepository.activeGame().value ?: throw Exception("No active game")
        if (game.termination() != null) {
            return
        }

        val delayedMoveTime = System.currentTimeMillis() + (500 + (0..1000).random())
        val move = engine.getMove(FenParam(game.fen()))

        delay(max(0, delayedMoveTime - System.currentTimeMillis()))
        game.move(move)
    }
}
