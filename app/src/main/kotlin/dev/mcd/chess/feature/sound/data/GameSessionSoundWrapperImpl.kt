package dev.mcd.chess.feature.sound.data

import com.github.bhlangonijr.chesslib.Piece
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.feature.sound.GameSessionSounds
import dev.mcd.chess.feature.sound.domain.BoardSoundPlayer
import dev.mcd.chess.feature.sound.domain.GameSessionSoundWrapper
import dev.mcd.chess.feature.sound.domain.SoundSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class GameSessionSoundWrapperImpl @Inject constructor(
    @GameSessionSounds
    private val context: CoroutineContext,
    private val soundPlayer: BoardSoundPlayer,
) : GameSessionSoundWrapper {

    override suspend fun attachSession(session: GameSession, settings: SoundSettings): Job {
        return if (settings.enabled) {
            CoroutineScope(coroutineContext).launch {
                val sessionJob = Job()
                launch(context + sessionJob) {
                    if (settings.enableNotify) {
                        soundPlayer.playNotify()
                        session.awaitTermination()
                        soundPlayer.playNotify()
                        sessionJob.cancel()
                    }
                }
                launch(context + sessionJob) {
                    session.moves().collectLatest { (move, _) ->
                        if (move.capturedPiece != Piece.NONE) {
                            soundPlayer.playCapture()
                        } else {
                            soundPlayer.playMove()
                        }
                    }
                }
            }
        } else {
            Job().apply { complete() }
        }
    }
}
