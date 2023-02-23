package dev.mcd.chess.domain

sealed interface Environment {
    val apiUrl: String

    object Production : Environment {
        override val apiUrl = "https://chess.mcd.dev"
    }

    data class Debug(
        override val apiUrl: String,
    ) : Environment
}

