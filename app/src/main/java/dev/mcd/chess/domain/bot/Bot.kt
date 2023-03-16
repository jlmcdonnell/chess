package dev.mcd.chess.domain.bot

import dev.mcd.chess.common.player.Player
import dev.mcd.chess.common.player.PlayerImage

class Bot(
    override val name: String,
    override val image: PlayerImage,
    val slug: String,
    val depth: Int,
    val level: Int,
) : Player

val bots = listOf(
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

fun String.botFromSlug() = bots.first { it.slug == this }
