package dev.mcd.chess.domain.game.bot

import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.domain.game.GameMessage
import dev.mcd.chess.domain.game.online.GameChannel
import kotlinx.coroutines.channels.ReceiveChannel

class BotRemoteGameChannel : GameChannel {
    override val incoming: ReceiveChannel<GameMessage>
        get() = TODO("Not yet implemented")

    override suspend fun requestGameState() {
        TODO("Not yet implemented")
    }

    override suspend fun resign() {
        TODO("Not yet implemented")
    }

    override suspend fun move(move: Move) {
        TODO("Not yet implemented")
    }

}
