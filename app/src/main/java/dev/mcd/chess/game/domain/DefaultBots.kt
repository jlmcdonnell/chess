package dev.mcd.chess.game.domain

import dev.mcd.chess.common.player.PlayerImage

object DefaultBots {
    fun bots() = listOf(
        Bot(
            slug = "level-1",
            name = "Blunderbot",
            image = PlayerImage.None,
            depth = 1,
            level = 0,
        ),
        Bot(
            slug = "level-2",
            name = "The Bishop of Banterbury",
            image = PlayerImage.None,
            depth = 2,
            level = 0,
        ),
        Bot(
            slug = "level-3",
            name = "Knight Rider",
            image = PlayerImage.None,
            depth = 2,
            level = 3,
        ),
        Bot(
            slug = "level-4",
            name = "Endgame Eddie",
            image = PlayerImage.None,
            depth = 5,
            level = 20,
        ),
        Bot(
            slug = "level-5",
            name = "Checkmate Chad",
            image = PlayerImage.None,
            depth = 15,
            level = 20,
        ),
    )

    fun fromSlug(slug: String) = bots().first { it.slug == slug }
}
