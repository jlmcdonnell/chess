package dev.mcd.chess.common.engine

sealed interface EngineCommand {

    data class SetPosition(private val fen: String) : EngineCommand {
        override fun toString() = "position fen $fen"
    }

    data class GoDepth(private val depth: Int) : EngineCommand {
        override fun toString() = "go depth $depth"
    }

    object GoNodes : EngineCommand {
        override fun toString() = "go nodes 1"
    }

    object Quit : EngineCommand {
        override fun toString() = "quit"
    }
}
