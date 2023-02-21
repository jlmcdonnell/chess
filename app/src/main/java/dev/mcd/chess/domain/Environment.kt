package dev.mcd.chess.domain

sealed interface Environment {
    val host: String

    object Production : Environment {
        override val host = "https://chess.mcd.dev"
    }

    data class Debug(override val host: String) : Environment
}

