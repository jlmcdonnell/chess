package dev.mcd.chess.ui.compose

import androidx.compose.runtime.Stable

/**
 * https://chris.banes.me/posts/composable-metrics/
 *
 * Used to wrap a value that is stable across recompositions.
 */
@Stable
class StableHolder<T>(val item: T) {
    operator fun component1(): T = item
}
