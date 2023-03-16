package dev.mcd.chess.common.engine

sealed interface EngineCommand {
    fun string(): String

    data class SetPosition(private val fen: String) : EngineCommand {
        override fun string() = "position fen $fen"
    }

    data class Go(private val depth: Int) : EngineCommand {
        override fun string() = "go depth $depth"
    }

    data class SetSkillLevel(private val level: Int) : EngineCommand {
        override fun string() = "setoption name Skill Level value $level"
    }
}
