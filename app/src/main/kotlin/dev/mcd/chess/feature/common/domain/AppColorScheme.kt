package dev.mcd.chess.feature.common.domain

enum class AppColorScheme {
    Blue, Brown;

    companion object {
        fun default(): AppColorScheme = Blue
    }
}
