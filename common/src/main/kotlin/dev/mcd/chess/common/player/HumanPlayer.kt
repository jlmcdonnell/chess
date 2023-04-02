package dev.mcd.chess.common.player

data class HumanPlayer(
    override val name: String = "",
    override val image: PlayerImage = PlayerImage.Default,
    val rating: Int = 0,
) : Player
