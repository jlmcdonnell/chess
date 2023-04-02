package dev.mcd.chess.ui.game.board.sounds

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReusableContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.github.bhlangonijr.chesslib.Piece
import dev.mcd.chess.R
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.ui.LocalGameSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun BoardSounds() {
    val context = LocalContext.current
    val session by LocalGameSession.current.sessionUpdates().collectAsState(GameSession())

    ReusableContent(session.id) {
        if (session.id.isNotEmpty()) {
            LaunchedEffect(session.id) {
                with(BoardSoundHandler) {
                    init(context)
                    notify()
                    launch {
                        awaitMoves(session)
                    }
                    launch {
                        awaitTermination(session)
                    }
                    awaitMoves(session)
                }
            }
        }
    }
}

private object BoardSoundHandler {

    private val attributes = AudioAttributes.Builder()
        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
        .build()

    private lateinit var capturePlayer: MediaPlayer
    private lateinit var movePlayer: MediaPlayer
    private lateinit var notifyPlayer: MediaPlayer

    fun init(context: Context) {
        if (::capturePlayer.isInitialized) {
            return
        }

        capturePlayer = MediaPlayer.create(context, R.raw.capture, attributes, 0)
        movePlayer = MediaPlayer.create(context, R.raw.move, attributes, 0)
        notifyPlayer = MediaPlayer.create(context, R.raw.notify, attributes, 0)
    }

    suspend fun notify() {
        withContext(Dispatchers.Default) {
            notifyPlayer.seekTo(0)
            notifyPlayer.start()
        }
    }

    suspend fun awaitTermination(session: GameSession) {
        withContext(Dispatchers.Default) {
            session.awaitTermination()
            notify()
        }
    }

    suspend fun awaitMoves(session: GameSession) {
        withContext(Dispatchers.Default) {
            session.moves().collectLatest { (move, _) ->
                if (move.capturedPiece != Piece.NONE) {
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
