package dev.mcd.chess.feature.engine

sealed class EngineProxyException(override val message: String) : Exception(message) {
    object EngineKilledException : EngineProxyException("Engine process killed")
}
