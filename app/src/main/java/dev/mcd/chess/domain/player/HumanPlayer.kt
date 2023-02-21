package dev.mcd.chess.domain.player

data class HumanPlayer(
    override val name: String,
    override val image: PlayerImage,
    val rating: Int,
) : Player
