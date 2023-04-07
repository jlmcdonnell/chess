package dev.mcd.chess.feature.game.domain

import dev.mcd.chess.common.player.Bot
import dev.mcd.chess.common.player.PlayerImage
import dev.mcd.chess.engine.lc0.MaiaWeights

object DefaultBots {
    fun bots() = listOf(
        Bot(
            slug = MaiaWeights.ELO_1100.name,
            name = "Pawn Pioneer",
            image = PlayerImage.Bot,
        ),
        Bot(
            slug = MaiaWeights.ELO_1200.name,
            name = "The Bishop of Banterbury",
            image = PlayerImage.Bot,
        ),
        Bot(
            slug = MaiaWeights.ELO_1300.name,
            name = "Knight Rider",
            image = PlayerImage.Bot,
        ),
        Bot(
            slug = MaiaWeights.ELO_1400.name,
            name = "Rook Rampage",
            image = PlayerImage.Bot,
        ),
        Bot(
            slug = MaiaWeights.ELO_1900.name,
            name = "Checkmate Chad",
            image = PlayerImage.Bot,
        ),
    )

    fun fromSlug(slug: String) = bots().first { it.slug == slug }
}
