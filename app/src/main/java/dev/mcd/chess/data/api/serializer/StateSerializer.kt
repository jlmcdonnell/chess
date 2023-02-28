package dev.mcd.chess.data.api.serializer

import kotlinx.serialization.Serializable
import dev.mcd.chess.domain.game.GameState as DomainState

@Serializable
enum class State {
    STARTED,
    DRAW,
    WHITE_RESIGNED,
    BLACK_RESIGNED,
    WHITE_CHECKMATED,
    BLACK_CHECKMATED,
}

fun State.domain() = DomainState.valueOf(name)
