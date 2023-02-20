package dev.mcd.chess.domain.model

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
        name = "Bot (1)",
        image = PlayerImage.None,
        depth = 1,
        level = 0,
    ),
    Bot(
        slug = "level-2",
        name = "Bot (2)",
        image = PlayerImage.None,
        depth = 2,
        level = 0,
    ),
    Bot(
        slug = "level-3",
        name = "Bot (3)",
        image = PlayerImage.None,
        depth = 2,
        level = 3,
    ),
    Bot(
        slug = "level-4",
        name = "Bot (4)",
        image = PlayerImage.None,
        depth = 5,
        level = 20,
    ),
    Bot(
        slug = "level-5",
        name = "Bot (5)",
        image = PlayerImage.None,
        depth = 15,
        level = 20,
    ),
)

fun String.botFromSlug() = bots.first { it.slug == this }
