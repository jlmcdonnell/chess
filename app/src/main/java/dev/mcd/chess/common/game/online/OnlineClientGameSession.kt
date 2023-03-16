package dev.mcd.chess.common.game.online

import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.OnlineGameChannel
import dev.mcd.chess.common.game.local.ClientGameSession
import dev.mcd.chess.common.player.Player

open class OnlineClientGameSession(
    id: String,
    self: Player,
    selfSide: Side,
    opponent: Player,
    val channel: OnlineGameChannel,
) : ClientGameSession(id, self, selfSide, opponent)
