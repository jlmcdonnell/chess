package dev.mcd.chess.feature.game.data.usecase

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Constants
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.common.engine.ChessEngine
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.common.player.Bot
import dev.mcd.chess.common.player.HumanPlayer
import dev.mcd.chess.common.player.PlayerImage
import dev.mcd.chess.feature.common.domain.Translations
import dev.mcd.chess.feature.game.domain.GameSessionRepository
import dev.mcd.chess.feature.game.domain.usecase.MoveForBot
import dev.mcd.chess.feature.game.domain.usecase.StartBotGame
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class StartBotGameImpl @Inject constructor(
    private val engine: ChessEngine,
    private val gameSessionRepository: GameSessionRepository,
    private val moveForBot: MoveForBot,
    private val translations: Translations,
) : StartBotGame {
    override suspend fun invoke(side: Side, bot: Bot) {
        engine.awaitReady()

        Timber.d("Engine ready")

        val board = Board().apply {
            loadFromFen(Constants.startStandardFENPosition)
        }
        val game = GameSession(
            id = UUID.randomUUID().toString(),
            self = HumanPlayer(
                name = translations.playerYou,
                image = PlayerImage.Default,
            ),
            selfSide = side,
            opponent = bot,
        )
        game.setBoard(board)
        gameSessionRepository.updateActiveGame(game)

        if (board.sideToMove != game.selfSide) {
            moveForBot()
        }
    }
}
