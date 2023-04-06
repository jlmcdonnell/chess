package dev.mcd.chess.feature.game.domain

import dev.mcd.chess.common.player.Bot
import dev.mcd.chess.common.player.PlayerImage

object DefaultBots {
    fun bots() = listOf(
        Bot(
            slug = "level-1",
            name = "Pawn Pioneer",
            image = PlayerImage.Bot,
            depth = 1,
        ),
        Bot(
            slug = "level-2",
            name = "The Bishop of Banterbury",
            image = PlayerImage.Bot,
            depth = 2,
        ),
        Bot(
            slug = "level-3",
            name = "Knight Rider",
            image = PlayerImage.Bot,
            depth = 3,
        ),
        Bot(
            slug = "level-4",
            name = "Rook Rampage",
            image = PlayerImage.Bot,
            depth = 4,
        ),
        Bot(
            slug = "level-5",
            name = "Checkmate Chad",
            image = PlayerImage.Bot,
            depth = 7,
        ),
    )

    fun fromSlug(slug: String) = bots().first { it.slug == slug }
}
