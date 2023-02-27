package dev.mcd.chess.domain.api

interface DebugHostStore {
    suspend fun host(): String
    suspend fun setHost(host: String)
}
