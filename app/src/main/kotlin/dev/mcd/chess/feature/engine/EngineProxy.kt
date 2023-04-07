package dev.mcd.chess.feature.engine

interface EngineProxy<MoveParams> {
    suspend fun start()
    suspend fun stop()
    suspend fun getMove(params: MoveParams): String
}
