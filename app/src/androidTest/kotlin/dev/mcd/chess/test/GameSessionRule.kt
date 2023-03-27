package dev.mcd.chess.test

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.common.player.HumanPlayer
import dev.mcd.chess.common.player.PlayerImage
import kotlinx.coroutines.runBlocking
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

fun createGameSessionRule(
    white: HumanPlayer = HumanPlayer("white", PlayerImage.None, 0),
    black: HumanPlayer = HumanPlayer("black", PlayerImage.None, 0),
    board: Board = Board(),
): GameSessionRule {
    return GameSessionRule(white, black, board)
}

class GameSessionRule(
    val white: HumanPlayer,
    val black: HumanPlayer,
    val board: Board,
) : TestRule {

    lateinit var game: GameSession

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                game = GameSession(
                    id = "id",
                    self = white,
                    selfSide = Side.WHITE,
                    opponent = black,
                )

                runBlocking { game.setBoard(board) }

                base.evaluate()
            }
        }
    }
}
