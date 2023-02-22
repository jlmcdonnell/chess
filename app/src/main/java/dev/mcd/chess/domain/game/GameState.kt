package dev.mcd.chess.domain.game

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Serializable
@Keep
enum class GameState {
    Started,
    Terminated,
}
