package dev.mcd.chess.common.player

class Bot(
    override val name: String,
    override val image: PlayerImage,
    val slug: String,
    val depth: Int,
) : Player
