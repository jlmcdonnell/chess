package dev.mcd.chess.feature.sound.domain

import dev.mcd.chess.common.game.GameSession

interface GameSessionSoundWrapper {
    suspend fun attachSession(session: GameSession, settings: SoundSettings)
}
