package dev.mcd.chess.game.domain

import dev.mcd.chess.common.player.Player
import dev.mcd.chess.common.player.PlayerImage

class Bot(
    override val name: String,
    override val image: PlayerImage,
    val slug: String,
    val depth: Int,
    val level: Int,
) : Player
