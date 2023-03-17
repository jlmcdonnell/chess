@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.mcd.chess.ui.game.board

import com.github.bhlangonijr.chesslib.MoveBackup
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.common.game.local.ClientGameSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class GameSessionManager {

    private val sessionUpdates = MutableStateFlow<ClientGameSession?>(null)

    suspend fun updateSession(session: ClientGameSession) {
        sessionUpdates.emit(session)
    }

    fun sessionUpdates(): Flow<ClientGameSession> = sessionUpdates.filterNotNull()

    fun pieceUpdates() = sessionUpdates.filterNotNull()
        .flatMapLatest { it.pieceUpdates() }

    fun moveUpdates() = sessionUpdates
        .filterNotNull()
        .flatMapLatest { it.moves() }

    fun captures(): Flow<List<Piece>> = channelFlow<List<Piece>> {
        sessionUpdates.collectLatest { session ->
            if (session == null) {
                send(emptyList())
            } else {
                send(session.captures())
                session.moves().map {
                    session.captures()
                }.collectLatest {
                    send(it)
                }
            }
        }
    }.distinctUntilChanged()

    fun legalMoves(): List<Move> {
        return sessionUpdates.value?.legalMoves() ?: emptyList()
    }

    fun promotions(move: Move): List<Move> {
        return sessionUpdates.value?.promotions(move) ?: emptyList()
    }

    fun lastMove(): MoveBackup? = sessionUpdates.value?.lastMove()

}
