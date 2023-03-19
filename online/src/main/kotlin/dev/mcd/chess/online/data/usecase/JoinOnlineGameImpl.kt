package dev.mcd.chess.online.data.usecase

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.game.GameResult
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.game.TerminationReason
import dev.mcd.chess.common.player.HumanPlayer
import dev.mcd.chess.common.player.PlayerImage
import dev.mcd.chess.common.player.UserId
import dev.mcd.chess.online.domain.AuthStore
import dev.mcd.chess.online.domain.ChessApi
import dev.mcd.chess.online.domain.OnlineGameSession
import dev.mcd.chess.online.domain.OnlineGameChannel
import dev.mcd.chess.online.domain.entity.GameMessage
import dev.mcd.chess.online.domain.usecase.JoinOnlineGame
import dev.mcd.chess.online.domain.usecase.JoinOnlineGame.Event.NewSession
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

internal class JoinOnlineGameImpl @Inject constructor(
    private val chessApi: ChessApi,
    private val authStore: AuthStore,
) : JoinOnlineGame {

    override suspend fun invoke(id: GameId) = channelFlow {
        val userId = authStore.userId() ?: throw Exception("No user ID")
        val authToken = authStore.token() ?: throw Exception("No auth token")

        chessApi.joinGame(authToken, id) {
            var session: GameSession? = null


            requestGameState()

            for (message in incoming) {
                when (message) {
                    is GameMessage.GameState -> {
                        if (session == null) {
                            session = createClientSession(
                                gameId = message.id,
                                userId = userId,
                                whitePlayer = message.whitePlayer,
                                blackPlayer = message.blackPlayer,
                                channel = this
                            )
                            session.setBoard(message.board)
                            send(NewSession(session))
                        } else {
                            val event = syncWithRemote(message.board, message.result, session)
                            event?.let { send(event) }
                        }
                    }

                    is GameMessage.MoveMessage -> {
                        val moved = session?.move(message.move, requireMoveCount = message.count) == true
                        if (!moved) {
                            requestGameState()
                        }
                    }

                    is GameMessage.ErrorNotUsersMove,
                    is GameMessage.ErrorGameTerminated,
                    is GameMessage.ErrorInvalidMove -> {
                        send(JoinOnlineGame.Event.FatalError(message::class.simpleName!!))
                    }
                }
            }
        }
    }

    private fun createClientSession(
        gameId: GameId,
        userId: UserId,
        whitePlayer: UserId,
        blackPlayer: UserId,
        channel: OnlineGameChannel,
    ): GameSession {
        val opponentId = if (userId == whitePlayer) blackPlayer else whitePlayer
        return OnlineGameSession(
            id = gameId,
            self = HumanPlayer(
                name = userId,
                image = PlayerImage.None,
                rating = 0,
            ),
            opponent = HumanPlayer(
                name = opponentId,
                image = PlayerImage.None,
                rating = 0,
            ),
            selfSide = if (userId == whitePlayer) Side.WHITE else Side.BLACK,
            channel = channel,
        )
    }

    private suspend fun syncWithRemote(
        remoteBoard: Board,
        result: GameResult,
        localSession: GameSession
    ): JoinOnlineGame.Event? {
        val board = remoteBoard.clone()
        localSession.setBoard(board)

        val matedOrDraw = board.let { it.isDraw || it.isMated }

        val reason: TerminationReason? = when (result) {
            GameResult.ONGOING -> null
            GameResult.DRAW -> TerminationReason(draw = true)
            GameResult.BLACK_WON -> {
                if (matedOrDraw) {
                    TerminationReason(sideMated = Side.WHITE)
                } else {
                    TerminationReason(resignation = Side.WHITE)
                }
            }

            GameResult.WHITE_WON -> {
                if (matedOrDraw) {
                    TerminationReason(sideMated = Side.BLACK)
                } else {
                    TerminationReason(resignation = Side.BLACK)
                }
            }
        }

        return reason?.let { JoinOnlineGame.Event.Termination(reason) }
    }
}
