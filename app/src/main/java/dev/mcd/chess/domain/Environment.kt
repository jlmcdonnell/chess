package dev.mcd.chess.domain

sealed interface Environment {
    val apiHost: String
    val apiScheme: String
    val apiPort: Int

    object Production : Environment {
        override val apiScheme = "https"
        override val apiPort = 443
        override val apiHost = "chess.mcd.dev"
    }

    data class Debug(
        override val apiHost: String,
        override val apiScheme: String,
        override val apiPort: Int,
    ) : Environment
}

