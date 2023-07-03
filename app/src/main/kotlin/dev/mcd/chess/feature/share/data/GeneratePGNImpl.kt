package dev.mcd.chess.feature.share.data

import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.game.Event
import com.github.bhlangonijr.chesslib.game.Game
import com.github.bhlangonijr.chesslib.game.GameResult
import com.github.bhlangonijr.chesslib.game.GenericPlayer
import com.github.bhlangonijr.chesslib.game.Player
import com.github.bhlangonijr.chesslib.game.Round
import com.github.bhlangonijr.chesslib.move.MoveList
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.common.player.Bot
import dev.mcd.chess.common.player.HumanPlayer
import dev.mcd.chess.feature.common.domain.Translations
import dev.mcd.chess.feature.share.domain.GeneratePGN
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import javax.inject.Inject

class GeneratePGNImpl @Inject constructor(
    private val provideDate: () -> LocalDate,
    private val translations: Translations,
) : GeneratePGN {

    override operator fun invoke(session: GameSession): String {
        val event = Event().apply {
            startDate = provideDate().format(pgnDateFormatter)
            site = translations.pgnSiteName
            name = session.mapEventName()
        }
        val round = Round(event)
        val game = Game(session.id, round).apply {
            moveText = StringBuilder()
            halfMoves = session.mapMoves()
            whitePlayer = session.mapWhitePlayer()
            blackPlayer = session.mapBlackPlayer()
            result = session.mapResult()
            plyCount = session.board().moveCounter.toString()
        }
        return game.toPgn(false, false)
    }

    private fun GameSession.mapMoves(): MoveList {
        return MoveList().apply {
            board().backup.forEach {
                add(it.move)
            }
        }
    }

    private fun GameSession.mapWhitePlayer(): Player {
        return if (selfSide == Side.WHITE) {
            self.map()
        } else {
            opponent.map()
        }
    }

    private fun GameSession.mapBlackPlayer(): Player {
        return if (selfSide == Side.BLACK) {
            self.map()
        } else {
            opponent.map()
        }
    }

    private fun dev.mcd.chess.common.player.Player.map(): Player {
        return GenericPlayer(null, name)
    }

    private fun GameSession.mapResult(): GameResult {
        return termination()?.let {
            if (it.sideMated == Side.WHITE) {
                GameResult.BLACK_WON
            } else if (it.sideMated == Side.BLACK) {
                GameResult.WHITE_WON
            } else if (it.draw) {
                GameResult.DRAW
            } else if (it.resignation == Side.WHITE) {
                GameResult.BLACK_WON
            } else if (it.resignation == Side.BLACK) {
                GameResult.WHITE_WON
            } else {
                GameResult.ONGOING
            }
        } ?: GameResult.ONGOING
    }

    private fun GameSession.mapEventName(): String {
        return when (opponent) {
            is Bot -> translations.pgnBotName
            is HumanPlayer -> translations.pgnOnlineGame
            else -> translations.pgnAnalysis
        }
    }

    companion object {
        private val pgnDateFormatter = DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR)
            .appendLiteral('.')
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendLiteral('.')
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .toFormatter()
    }
}
