package dev.mcd.chess.feature.share

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.common.player.HumanPlayer
import dev.mcd.chess.feature.common.domain.Translations
import dev.mcd.chess.feature.share.data.GeneratePGNImpl
import io.kotest.common.runBlocking
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class GeneratePGNImplTest : StringSpec(
    {
        isolationMode = IsolationMode.InstancePerLeaf

        val date = LocalDate.of(1980, 2, 3)
        val siteName = "test"
        val eventNameOnlineGame = "Online Game"
        val translations = mockk<Translations> {
            every { pgnSiteName } returns siteName
            every { pgnOnlineGame } returns eventNameOnlineGame
        }
        val generatePGN = GeneratePGNImpl(
            translations = translations,
            provideDate = { date },
        )
        val session = GameSession(
            id = "id",
            self = HumanPlayer("white"),
            opponent = HumanPlayer("black"),
            selfSide = Side.WHITE,
        ).apply {
            runBlocking { setBoard(Board()) }
        }

        "Generate PGN with moves in ongoing game" {
            session.move("e2e4")
            session.move("e7e5")

            val pgn = generatePGN(session)
            pgn shouldContain "1. e4 e5 *"
            pgn shouldContain "[White \"white\"]"
            pgn shouldContain "[Black \"black\"]"
        }

        "Generate PGN for checkmate" {
            session.apply {
                move("e2e4")
                move("e7e5")
                move("d1h5")
                move("e8e7")
                move("h5e5")
            }

            val pgn = generatePGN(session)
            pgn shouldContain "1. e4 e5 2. Qh5 Ke7 3. Qxe5# 1-0"
        }

        "Generate PGN for draw" {
            session.apply {
                move("g1f3")
                move("g8f6")
                move("f3g1")
                move("f6g8")
                move("g1f3")
                move("g8f6")
                move("f3g1")
                move("f6g8")
            }

            val pgn = generatePGN(session)
            pgn shouldContain "1. Nf3 Nf6 2. Ng1 Ng8 3. Nf3 Nf6 4. Ng1 Ng8 1/2-1/2"
        }

        "Generate PGN for resignation" {
            session.apply {
                move("e2e4")
                resign()
            }
            val pgn = generatePGN(session)
            pgn shouldContain "1. e4 0-1"
        }

        "Generate PGN with date" {
            val pgn = generatePGN(session)
            pgn shouldContain "[Date \"1980.02.03\"]"
        }

        "Generate PGN with ply count" {
            session.move("e2e4")
            session.move("e7e5")

            val pgn = generatePGN(session)
            pgn shouldContain "[PlyCount \"2\"]"
        }

        "Generate PGN with site" {
            val pgn = generatePGN(session)
            pgn shouldContain "[Site \"$siteName\"]"
            print(pgn)
        }

        "Generate PGN with event" {
            val pgn = generatePGN(session)
            pgn shouldContain "[Event \"$eventNameOnlineGame\"]"
            print(pgn)
        }
    },
)
