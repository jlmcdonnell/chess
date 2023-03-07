package dev.mcd.chess.data.game.online

import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.game.GameResult
import dev.mcd.chess.data.api.ApiCredentialsStore
import dev.mcd.chess.domain.api.ChessApi
import dev.mcd.chess.domain.game.GameId
import dev.mcd.chess.domain.game.GameMessage
import dev.mcd.chess.domain.game.TerminationReason
import dev.mcd.chess.domain.game.local.ClientGameSession
import dev.mcd.chess.domain.game.local.GameSessionRepository
import dev.mcd.chess.domain.game.online.GameSession
import dev.mcd.chess.domain.game.online.JoinOnlineGame
import dev.mcd.chess.domain.game.online.OnlineClientGameSession
import dev.mcd.chess.domain.game.online.OnlineGameChannel
import dev.mcd.chess.domain.game.online.opponent
import dev.mcd.chess.domain.game.online.sideForUser
import dev.mcd.chess.domain.player.HumanPlayer
import dev.mcd.chess.domain.player.PlayerImage
import dev.mcd.chess.domain.player.UserId
import kotlinx.coroutines.flow.channelFlow
import timber.log.Timber
import javax.inject.Inject

class JoinOnlineGameImpl @Inject constructor(
    private val chessApi: ChessApi,
    private val gameSessionRepository: GameSessionRepository,
    private val apiCredentialsStore: ApiCredentialsStore,
) : JoinOnlineGame {

    override suspend fun invoke(id: GameId) = channelFlow {
        val userId = apiCredentialsStore.userId() ?: throw Exception("No user ID")
        var session = chessApi.game(id)

        chessApi.joinGame(session.id) {
            Timber.d("Joined game ${session.id}")
            val clientSession = createClientSession(userId, session, channel = this)
            clientSession.setBoard(session.game.board)
            gameSessionRepository.updateActiveGame(clientSession)

            requestGameState()

            for (message in incoming) {
                Timber.d("Received message: ${message::class.simpleName}")
                when (message) {
                    is GameMessage.GameState -> {
                        session = message.session
                        val event = syncWithRemote(session, clientSession)
                        event?.let { send(event) }
                    }
                    is GameMessage.MoveMessage -> {
                        if (!clientSession.doMove(message.move, requireMoveCount = message.count)) {
                            Timber.e("Board is out of sync. Unable to make move ${message.move}")
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
        userId: UserId,
        session: GameSession,
        channel: OnlineGameChannel,
    ): ClientGameSession {
        return OnlineClientGameSession(
            id = session.id,
            self = HumanPlayer(
                name = userId,
                image = PlayerImage.None,
                rating = 0,
            ),
            opponent = HumanPlayer(
                name = session.opponent(userId),
                image = PlayerImage.None,
                rating = 0,
            ),
            selfSide = session.sideForUser(userId),
            channel = channel,
        )
    }

    private suspend fun syncWithRemote(onlineSession: GameSession, localSession: ClientGameSession): JoinOnlineGame.Event? {
        val board = onlineSession.game.board.clone()
        localSession.setBoard(board)

        val matedOrDraw = board.let { it.isDraw || it.isMated }

        val reason: TerminationReason? = when (onlineSession.game.result!!) {
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
