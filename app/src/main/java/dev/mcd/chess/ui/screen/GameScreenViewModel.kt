package dev.mcd.chess.ui.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Constants
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.move.Move
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.mcd.chess.data.StockfishAdapter
import dev.mcd.chess.domain.GameSession
import dev.mcd.chess.domain.GameSessionRepository
import dev.mcd.chess.domain.model.Bot
import dev.mcd.chess.domain.model.HumanPlayer
import dev.mcd.chess.domain.model.PlayerImage
import dev.mcd.chess.domain.model.TerminationReason
import dev.mcd.chess.domain.model.botFromSlug
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    private val stockfish: StockfishAdapter,
    private val gameSessionRepository: GameSessionRepository,
    private val state: SavedStateHandle,
) : ViewModel(), ContainerHost<GameScreenViewModel.State, GameScreenViewModel.SideEffect> {

    private lateinit var bot: Bot

    override val container = container<State, SideEffect>(State.Loading) {
        viewModelScope.launch {
            gameSessionRepository.activeGame()
                .filterNotNull()
                .collectLatest { session ->
                    intent {
                        reduce { State.Game(game = session) }
                    }
                }
        }
        bot = state.get<String>("bot")!!.botFromSlug()
        startGame()
    }

    fun onResign() {
        intent {
            val game = gameSessionRepository.activeGame().firstOrNull() ?: return@intent
            endGame(game)
        }
    }

    fun onPlayerMove(move: Move) {
        intent {
            val game = gameSessionRepository.activeGame().firstOrNull() ?: return@intent
            val board = game.board
            if (board.sideToMove == game.selfSide && move in board.legalMoves()) {
                println("Moving for player")
                board.doMove(move)
                tryMoveBot(game)
            }
        }
    }

    private suspend fun tryMoveBot(game: GameSession) {
        val board = game.board
        if (!board.isMated && !board.isDraw) {
            println("Moving for stockfish")
            val delayedMoveTime = System.currentTimeMillis() + (500 + (0..1000).random())
            val stockfishMoveSan = stockfish.getMove(board.fen, level = bot.level, depth = bot.depth)
            val stockfishMove = Move(stockfishMoveSan, board.sideToMove)
            val delay = delayedMoveTime - System.currentTimeMillis()
            if (delay > 0) {
                delay(delay)
            }

            board.doMove(stockfishMove)

            if (board.isMated || board.isDraw) {
                endGame(game)
            }
        } else {
            endGame(game)
        }
    }

    private fun startGame() {
        intent {
            val board = Board().apply {
                clear()
//                loadFromFen(Constants.startStandardFENPosition)
                loadFromFen("8/8/8/8/8/2k3p1/7p/2K5 b - - 0 1")
            }
            val game = GameSession(
                id = UUID.randomUUID().toString(),
                board = board,
                self = HumanPlayer(
                    name = "You",
                    rating = 900,
                    image = PlayerImage.None
                ),
                selfSide = Side.BLACK,
                opponent = bot
            )
            gameSessionRepository.updateActiveGame(game)

            if (board.sideToMove != game.selfSide) {
                tryMoveBot(game)
            }
        }
    }

    private fun endGame(game: GameSession) {
        intent {
            val board = game.board
            gameSessionRepository.updateActiveGame(null)

            val mated = board.sideToMove.takeIf { board.isMated }
            val draw = board.isDraw
            val resignation = if (mated == null && !draw) board.sideToMove else null

            reduce {
                (state as? State.Game)?.copy(terminated = true) ?: state
            }

            val reason = TerminationReason(
                sideMated = mated,
                draw = draw,
                resignation = resignation,
            )
            postSideEffect(SideEffect.AnnounceTermination(reason))
        }
    }

    sealed interface State {
        object Loading : State

        data class Game(
            val game: GameSession,
            val terminated: Boolean = false,
        ) : State
    }

    sealed interface SideEffect {
        data class AnnounceTermination(
            val reason: TerminationReason,
        ) : SideEffect
    }
}
