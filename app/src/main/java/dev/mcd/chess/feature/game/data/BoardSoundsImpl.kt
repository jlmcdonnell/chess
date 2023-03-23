package dev.mcd.chess.feature.game.data

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import com.github.bhlangonijr.chesslib.Piece
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.mcd.chess.R
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.feature.game.domain.BoardSounds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BoardSoundsImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
) : BoardSounds {

    private val attributes = AudioAttributes.Builder()
        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
        .build()

    private val capturePlayer by lazy { MediaPlayer.create(context, R.raw.capture, attributes, 0) }
    private val movePlayer by lazy { MediaPlayer.create(context, R.raw.move, attributes, 0) }
    private val notifyPlayer by lazy { MediaPlayer.create(context, R.raw.notify, attributes, 0) }

    override suspend fun notify() {
        withContext(Dispatchers.Default) {
            notifyPlayer.seekTo(0)
            notifyPlayer.start()
        }
    }

    override suspend fun awaitMoves(
        session: GameSession,
    ) {
        withContext(Dispatchers.Default) {
            session.moves().collectLatest { (move, _) ->
                if (session.termination() != null) {
                    notifyPlayer.seekTo(0)
                    notifyPlayer.start()
                } else if (move.capturedPiece != Piece.NONE) {
                    capturePlayer.seekTo(0)
                    capturePlayer.start()
                } else {
                    movePlayer.seekTo(0)
                    movePlayer.start()
                }
            }
        }
    }
}
