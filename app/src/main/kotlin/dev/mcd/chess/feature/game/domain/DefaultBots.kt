package dev.mcd.chess.feature.game.domain

import dev.mcd.chess.common.player.Bot
import dev.mcd.chess.common.player.PlayerImage

object DefaultBots {
    fun bots() = listOf(
        Bot(
            slug = "level-1",
            name = "Blunderbot",
            image = PlayerImage.Bot,
            depth = 1,
            level = 0,
        ),
        Bot(
            slug = "level-2",
            name = "The Bishop of Banterbury",
            image = PlayerImage.Bot,
            depth = 2,
            level = 0,
        ),
        Bot(
            slug = "level-3",
            name = "Knight Rider",
            image = PlayerImage.Bot,
            depth = 2,
            level = 3,
        ),
        Bot(
            slug = "level-4",
            name = "Endgame Eddie",
            image = PlayerImage.Bot,
            depth = 5,
            level = 20,
        ),
        Bot(
            slug = "level-5",
            name = "Checkmate Chad",
            image = PlayerImage.Bot,
            depth = 15,
            level = 20,
        ),
    )

    fun fromSlug(slug: String) = bots().first { it.slug == slug }
}
