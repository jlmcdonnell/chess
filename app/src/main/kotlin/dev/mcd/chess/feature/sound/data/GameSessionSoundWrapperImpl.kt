package dev.mcd.chess.feature.sound.data

import com.github.bhlangonijr.chesslib.Piece
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.feature.sound.GameSessionSounds
import dev.mcd.chess.feature.sound.domain.BoardSoundPlayer
import dev.mcd.chess.feature.sound.domain.GameSessionSoundWrapper
import dev.mcd.chess.feature.sound.domain.SoundSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameSessionSoundWrapperImpl @Inject constructor(
    @GameSessionSounds
    private val scope: CoroutineScope,
    private val soundPlayer: BoardSoundPlayer,
) : GameSessionSoundWrapper {
    override suspend fun attachSession(session: GameSession, settings: SoundSettings) {
        if (settings.enabled) {
            scope.launch {
                launch {
                    if (settings.enableNotify) {
                        soundPlayer.playNotify()
                        session.awaitTermination()
                        soundPlayer.playNotify()
                    }
                }
                launch {
                    session.moves().collectLatest { (move, _) ->
                        if (move.capturedPiece != Piece.NONE) {
                            soundPlayer.playCapture()
                        } else {
                            soundPlayer.playMove()
                        }
                    }
                }
            }
        }
    }
}
