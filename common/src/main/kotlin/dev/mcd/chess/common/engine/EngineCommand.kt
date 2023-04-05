package dev.mcd.chess.common.engine

sealed interface EngineCommand {
    fun string(): String

    data class SetPosition(private val fen: String) : EngineCommand {
        override fun string() = "position fen $fen"
    }

    data class Go(private val depth: Int) : EngineCommand {
        override fun string() = "go depth $depth"
    }

    object Quit : EngineCommand {
        override fun string() = "quit"
    }
}
