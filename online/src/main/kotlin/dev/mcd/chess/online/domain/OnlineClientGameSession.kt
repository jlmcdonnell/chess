package dev.mcd.chess.online.domain

import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.common.game.ClientGameSession
import dev.mcd.chess.common.player.Player

open class OnlineClientGameSession(
    id: String,
    self: Player,
    selfSide: Side,
    opponent: Player,
    val channel: OnlineGameChannel,
) : ClientGameSession(id, self, selfSide, opponent)
