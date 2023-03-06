package dev.mcd.chess.domain.game.online

import com.github.bhlangonijr.chesslib.move.Move
import dev.mcd.chess.domain.game.GameMessage
import kotlinx.coroutines.channels.ReceiveChannel

interface GameChannel {
    val incoming: ReceiveChannel<GameMessage>

    suspend fun requestGameState()
    suspend fun resign()
    suspend fun move(move: Move)
}
