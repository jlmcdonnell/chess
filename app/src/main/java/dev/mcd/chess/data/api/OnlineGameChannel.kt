package dev.mcd.chess.data.api

import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.domain.game.online.GameChannel
import dev.mcd.chess.domain.game.GameMessage
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import timber.log.Timber

class OnlineGameChannel(
    override val incoming: ReceiveChannel<GameMessage>,
    private val outgoing: SendChannel<String>,
) : GameChannel {
    override suspend fun requestGameState() {
        Timber.d("OUTGOING: state")
        outgoing.send("state")
    }

    override suspend fun resign() {
        Timber.d("OUTGOING: resign")
        outgoing.send("resign")
    }

    override suspend fun move(move: Move) {
        val moveCmd = move.toString()
        Timber.d("OUTGOING: $moveCmd")
        outgoing.send(moveCmd)
    }
}
