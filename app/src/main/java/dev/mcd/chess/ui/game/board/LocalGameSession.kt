@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.mcd.chess.ui.game.board

import androidx.compose.runtime.compositionLocalOf
import com.github.bhlangonijr.chesslib.MoveBackup
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.domain.game.local.ClientGameSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull

val LocalGameSession = compositionLocalOf { GameSessionManager() }

class GameSessionManager {
    val sessionUpdates = MutableStateFlow<ClientGameSession?>(null)
    val terminated = MutableStateFlow(false)

    fun pieceUpdates() = sessionUpdates.filterNotNull()
        .flatMapLatest { it.pieceUpdates() }

    fun moveUpdates() = sessionUpdates
        .filterNotNull()
        .flatMapLatest { it.moves() }

    fun captures() = moveUpdates().mapNotNull {
        sessionUpdates.value?.captures()
    }

    fun terminated() = terminated

    fun legalMoves(): List<Move> {
        return sessionUpdates.value?.legalMoves() ?: emptyList()
    }

    fun promotions(move: Move): List<Move> {
        return sessionUpdates.value?.promotions(move) ?: emptyList()
    }

    fun lastMove(): MoveBackup? = sessionUpdates.value?.lastMove()

}
