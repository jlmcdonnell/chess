package dev.mcd.chess.engine.lc0

import android.content.Context
import androidx.annotation.Keep
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.mcd.chess.common.engine.ChessEngine
import dev.mcd.chess.common.engine.EngineCommand
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

internal class Lc0Engine @Inject constructor(
    private val bridge: Lc0Jni,
    @ApplicationContext
    private val context: Context,
    private val engineContext: CoroutineContext,
) : ChessEngine {

    private val stateFlow = MutableStateFlow<State>(State.Uninitialized)
    private val weightsFile = File(context.dataDir, "maia-1100.pb")

    override fun init() {
        context.assets.open("weights/maia-1100.pb").copyTo(
            weightsFile.outputStream(),
        )
        bridge.init()
    }

    override suspend fun awaitReady() {
        awaitState<State.Ready>()
    }

    override suspend fun startAndWait() {
        awaitState<State.Uninitialized>()
        CoroutineScope(coroutineContext).launch {
            launch(engineContext) {
                bridge.main(weightsFile.absolutePath)
            }

            launch(engineContext) {
                while (isActive) {
                    val output = bridge.readLine() ?: continue
                    if (output.startsWith(BEST_MOVE_TOKEN)) {
                        // 0:bestmove 1:[e2e4] 2:ponder 3:a6a7
                        val move = output.split(" ")[1].trim()
                        assertStateOrNull<State.Moving>()?.completable?.complete(move)
                    }
                }
            }
            moveToState(State.Ready)
        }.let { job ->
            try {
                awaitCancellation()
            } finally {
                job.cancel()
                moveToState(State.Uninitialized)
            }
        }
    }

    override suspend fun getMove(fen: String, depth: Int): String {
        return withContext(engineContext) {
            awaitState<State.Ready>()
            val moveCompletable = CompletableDeferred<String>()
            moveToState(State.Moving(moveCompletable))

            listOf(
                EngineCommand.SetPosition(fen),
                EngineCommand.Go(depth),
            ).forEach {
                bridge.writeLine(it.string())
            }

            moveCompletable.await().also {
                moveToState(State.Ready)
            }
        }
    }

    private inline fun <reified T : State> assertStateOrNull(): T? {
        return stateFlow.value as? T
    }

    private suspend inline fun <reified T : State> awaitState() {
        Timber.tag("Lc0").d("Awaiting ${T::class.simpleName}")
        stateFlow.takeWhile { it !is T }.collect()
    }

    private suspend fun moveToState(state: State) {
        stateFlow.emit(state)
        Timber.tag("Lc0").d("Moved to ${state::class.simpleName}")
    }

    private sealed interface State {
        @Keep
        object Uninitialized : State

        @Keep
        class Moving(
            val completable: CompletableDeferred<String>,
        ) : State

        @Keep
        object Ready : State
    }

    companion object {
        internal const val BEST_MOVE_TOKEN = "bestmove"
        private const val DEFAULT_THREAD_COUNT = 4
    }
}
