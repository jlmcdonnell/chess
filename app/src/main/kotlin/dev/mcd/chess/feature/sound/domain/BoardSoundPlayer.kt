package dev.mcd.chess.feature.sound.domain

interface BoardSoundPlayer {
    suspend fun playNotify()
    suspend fun playCapture()
    suspend fun playMove()
}
