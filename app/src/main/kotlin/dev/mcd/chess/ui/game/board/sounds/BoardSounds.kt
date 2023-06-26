package dev.mcd.chess.ui.game.board.sounds

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.github.bhlangonijr.chesslib.Piece
import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.feature.sound.data.BoardSoundPlayerImpl
import dev.mcd.chess.feature.sound.domain.BoardSoundPlayer
import dev.mcd.chess.ui.LocalGameSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun BoardSounds(enableNotify: Boolean = true) {
    val context = LocalContext.current
    val session by LocalGameSession.current.sessionUpdates().collectAsState(GameSession())

    if (session.id.isNotEmpty()) {
        LaunchedEffect(session.id) {
            with(BoardSoundHandler) {
                init(context)
                if (enableNotify) {
                    notify()
                    launch {
                        awaitTermination(session)
                    }
                }
                launch {
                    awaitMoves(session)
                }
                awaitMoves(session)
            }
        }
    }
}

private object BoardSoundHandler {

    private lateinit var soundPlayer: BoardSoundPlayer

    fun init(context: Context) {
        if (::soundPlayer.isInitialized) {
            return
        }

        soundPlayer = BoardSoundPlayerImpl(context)
    }

    suspend fun notify() {
        withContext(Dispatchers.Default) {
            soundPlayer.playNotify()
        }
    }

    suspend fun awaitTermination(session: GameSession) {
        withContext(Dispatchers.Default) {
            session.awaitTermination()
            soundPlayer.playNotify()
        }
    }

    suspend fun awaitMoves(session: GameSession) {
        withContext(Dispatchers.Default) {
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
