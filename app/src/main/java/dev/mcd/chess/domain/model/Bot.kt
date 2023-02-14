package dev.mcd.chess.domain.model

import dev.mcd.chess.R

class Bot(
    override val name: String,
    override val image: PlayerImage,
    val slug: String,
    val depth: Int,
    val level: Int,
) : Player

val bots = listOf(
    Bot(
        slug = "mochi",
        name = "Mochi",
        image = PlayerImage.Local(R.drawable.mochi),
        depth = 3,
        level = 0,
    ),
)

fun String.botFromSlug() = bots.first { it.slug == this }
