package dev.mcd.chess.online.domain

import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.online.domain.entity.GameMessage
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

class OnlineGameChannel(
    val incoming: ReceiveChannel<GameMessage>,
    private val outgoing: SendChannel<String>,
) {
    suspend fun requestGameState() {
        outgoing.send(COMMAND_STATE)
    }

    suspend fun resign() {
        outgoing.send(COMMAND_RESIGN)
    }

    suspend fun move(move: Move) {
        val moveCmd = move.toString()
        outgoing.send(moveCmd)
    }

    companion object {
        private const val COMMAND_STATE = "state"
        private const val COMMAND_RESIGN = "resign"
    }
}
