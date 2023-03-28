package dev.mcd.chess.online.domain

import com.github.bhlangonijr.chesslib.Side
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.common.player.Player

open class OnlineGameSession(
    id: String,
    self: Player,
    opponent: Player,
    selfSide: Side,
    val channel: OnlineGameChannel,
) : GameSession(id, self, opponent, selfSide)
