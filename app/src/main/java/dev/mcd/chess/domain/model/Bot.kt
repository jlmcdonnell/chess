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
        slug = "bad-bot",
        name = "Bad Bot",
        image = PlayerImage.None,
        depth = 2,
        level = 0,
    ),
    Bot(
        slug = "good-bot",
        name = "Good Bot",
        image = PlayerImage.None,
        depth = 2,
        level = 1,
    ),
)

fun String.botFromSlug() = bots.first { it.slug == this }
