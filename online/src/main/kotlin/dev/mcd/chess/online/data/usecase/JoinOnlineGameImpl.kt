package dev.mcd.chess.online.data.usecase

import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.game.GameResult
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.common.game.MoveResult
import dev.mcd.chess.common.game.MoveString
import dev.mcd.chess.common.game.TerminationReason
import dev.mcd.chess.common.player.HumanPlayer
import dev.mcd.chess.common.player.PlayerImage
import dev.mcd.chess.common.player.UserId
import dev.mcd.chess.online.data.usecase.ValidateMoveFromRemote.Result.SyncRequiredApplyMove
import dev.mcd.chess.online.domain.AuthStore
import dev.mcd.chess.online.domain.ChessApi
import dev.mcd.chess.online.domain.OnlineGameChannel
import dev.mcd.chess.online.domain.OnlineGameSession
import dev.mcd.chess.online.domain.entity.GameMessage
import dev.mcd.chess.online.domain.usecase.JoinOnlineGame
import dev.mcd.chess.online.domain.usecase.JoinOnlineGame.Event.NewSession
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

internal class JoinOnlineGameImpl @Inject constructor(
    private val chessApi: ChessApi,
    private val authStore: AuthStore,
) : JoinOnlineGame {

    override suspend fun invoke(id: GameId) = channelFlow {
        val userId = authStore.userId() ?: throw Exception("No user ID")
        val authToken = authStore.token() ?: throw Exception("No auth token")

        chessApi.joinGame(authToken, id) {
            runCatching {
                val session: GameSession

                incoming.receiveAsFlow()
                    .filterIsInstance<GameMessage.GameState>()
                    .first()
                    .let { message ->
                        session = createClientSession(
                            gameId = message.id,
                            userId = userId,
                            whitePlayer = message.whitePlayer,
                            blackPlayer = message.blackPlayer,
                            board = message.board,
                            channel = this
                        )
                        send(NewSession(session))
                    }

                for (message in incoming) {
                    when (message) {
                        is GameMessage.GameState -> {
                            val event = syncWithRemote(message.board, message.result, session)
                            event?.let { send(event) }
                        }

                        is GameMessage.MoveMessage -> {
                            val validateResult = ValidateMoveFromRemote(MoveString(message.move), message.count, session)
                            if (validateResult == SyncRequiredApplyMove) {
                                val moveResult = session.move(message.move)
                                if (moveResult == MoveResult.MoveIllegal) {
                                    requestGameState()
                                }
                            }
                        }

                        is GameMessage.ErrorNotUsersMove,
                        is GameMessage.ErrorGameTerminated,
                        is GameMessage.ErrorInvalidMove -> {
                            send(JoinOnlineGame.Event.FatalError(message::class.simpleName!!))
                        }
                    }
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private suspend fun createClientSession(
        gameId: GameId,
        userId: UserId,
        whitePlayer: UserId,
        blackPlayer: UserId,
        board: Board,
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
        ).apply {
            setBoard(board)
        }
    }

    private suspend fun syncWithRemote(
        remoteBoard: Board,
        result: GameResult,
        localSession: GameSession
    ): JoinOnlineGame.Event? {
        val board = remoteBoard.clone()
        localSession.setBoard(board)
        return getTerminationReason(board, result)?.let { JoinOnlineGame.Event.Termination(it) }
    }

    private fun getTerminationReason(board: Board, result: GameResult): TerminationReason? {
        val matedOrDraw = board.let { it.isDraw || it.isMated }

        return when (result) {
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
    }
}
