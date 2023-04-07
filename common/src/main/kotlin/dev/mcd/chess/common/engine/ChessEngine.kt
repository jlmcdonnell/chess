package dev.mcd.chess.common.engine

interface ChessEngine<InitParams, MoveParams> {
    fun init(params: InitParams)
    suspend fun awaitReady()
    suspend fun startAndWait()
    suspend fun getMove(params: MoveParams): String
}
