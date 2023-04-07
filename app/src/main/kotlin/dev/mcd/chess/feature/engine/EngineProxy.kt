package dev.mcd.chess.feature.engine

interface EngineProxy<InitParams, MoveParams> {
    suspend fun start(initParams: InitParams)
    suspend fun stop()
    suspend fun getMove(params: MoveParams): String
}
