package dev.mcd.chess.feature.game.domain.usecase

import dev.mcd.chess.activity.engine.BotEngine
import dev.mcd.chess.common.player.Bot
import dev.mcd.chess.feature.engine.EngineProxy
import dev.mcd.chess.feature.game.domain.GameSessionRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.math.max

class MoveForBotImpl @Inject constructor(
    private val gameSessionRepository: GameSessionRepository,
    @BotEngine
    private val engine: EngineProxy,
) : MoveForBot {

    override suspend fun invoke() {
        val game = gameSessionRepository.activeGame().value ?: throw Exception("No active game")
        val bot = game.opponent as? Bot ?: throw Exception("Opponent is not bot")
        if (game.termination() != null) {
            return
        }

        val delayedMoveTime = System.currentTimeMillis() + (500 + (0..1000).random())
        val stockfishMoveSan = engine.getMove(game.fen(), depth = bot.depth)

        delay(max(0, delayedMoveTime - System.currentTimeMillis()))
        game.move(stockfishMoveSan)
    }
}
