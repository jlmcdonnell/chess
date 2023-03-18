package dev.mcd.chess.feature.online.domain

import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.OnlineGameChannel
import dev.mcd.chess.common.player.Player
import dev.mcd.chess.feature.game.domain.ClientGameSession

open class OnlineClientGameSession(
    id: String,
    self: Player,
    selfSide: Side,
    opponent: Player,
    val channel: OnlineGameChannel,
) : ClientGameSession(id, self, selfSide, opponent)
