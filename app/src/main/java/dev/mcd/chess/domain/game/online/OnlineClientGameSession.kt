package dev.mcd.chess.domain.game.online

import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.domain.game.local.ClientGameSession
import dev.mcd.chess.domain.player.Player

open class OnlineClientGameSession(
    id: String,
    self: Player,
    selfSide: Side,
    opponent: Player,
    val channel: OnlineGameChannel,
) : ClientGameSession(id, self, selfSide, opponent)
