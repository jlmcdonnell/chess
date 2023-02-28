package dev.mcd.chess.data.api

import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.domain.api.ActiveGame
import dev.mcd.chess.domain.game.GameMessage
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import timber.log.Timber

class ActiveGameImpl(
    override val incoming: ReceiveChannel<GameMessage>,
    private val outgoing: SendChannel<String>,
) : ActiveGame {
    override suspend fun requestMoveHistory() {
        Timber.d("OUTGOING: history")
        outgoing.send("history")
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
