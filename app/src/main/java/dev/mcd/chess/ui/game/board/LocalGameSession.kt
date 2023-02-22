@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.mcd.chess.ui.game.board

import androidx.compose.runtime.compositionLocalOf
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.MoveBackup
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.domain.game.LocalGameSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

val LocalGameSession = compositionLocalOf { GameSessionManager() }

class GameSessionManager {
    val sessionUpdates = MutableStateFlow<LocalGameSession?>(null)
    val terminated = MutableStateFlow(false)

    fun pieceUpdates() = sessionUpdates.filterNotNull()
        .flatMapLatest { it.pieceUpdates }

    fun moveUpdates() = sessionUpdates
        .filterNotNull()
        .flatMapLatest { it.moves.filterNotNull() }

    fun terminated() = terminated

    fun board(): Board? {
        return sessionUpdates.value?.board
    }

    fun moves() = board()?.legalMoves() ?: emptyList()

    fun promotions(move: Move) = board()
        ?.legalMoves()
        ?.filter { it.from == move.from && it.to == move.to }
        ?.filter { it.promotion != Piece.NONE } ?: emptyList()

    fun lastMove(): MoveBackup? = board()?.backup?.last
}
