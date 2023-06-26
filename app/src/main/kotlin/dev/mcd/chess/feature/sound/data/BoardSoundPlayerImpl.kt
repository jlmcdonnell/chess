package dev.mcd.chess.feature.sound.data

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import dev.mcd.chess.R
import dev.mcd.chess.feature.sound.domain.BoardSoundPlayer
import javax.inject.Inject

class BoardSoundPlayerImpl @Inject constructor(context: Context) : BoardSoundPlayer {

    private val attributes = AudioAttributes.Builder()
        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
        .build()

    private val capturePlayer: MediaPlayer
    private val movePlayer: MediaPlayer
    private val notifyPlayer: MediaPlayer

    init {
        capturePlayer = MediaPlayer.create(context, R.raw.capture, attributes, 0)
        movePlayer = MediaPlayer.create(context, R.raw.move, attributes, 0)
        notifyPlayer = MediaPlayer.create(context, R.raw.notify, attributes, 0)
    }

    override suspend fun playNotify() {
        notifyPlayer.seekTo(0)
        notifyPlayer.start()
    }

    override suspend fun playCapture() {
        capturePlayer.seekTo(0)
        capturePlayer.start()
    }

    override suspend fun playMove() {
        movePlayer.seekTo(0)
        movePlayer.start()
    }
}
