package dev.mcd.chess.engine.stockfish.data

import androidx.annotation.Keep
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
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

internal class StockfishEngine(
    private val bridge: StockfishJni,
    private val engineContext: CoroutineContext,
) : ChessEngine<Unit, FenAndDepth> {

    private val stateFlow = MutableStateFlow<State>(State.Uninitialized)

    override fun init(params: Unit) {
        bridge.init()
    }

    override suspend fun awaitReady() {
        awaitState<State.Ready>()
    }

    override suspend fun startAndWait() {
        awaitState<State.Uninitialized>()
        CoroutineScope(coroutineContext).launch {
            launch(engineContext) {
                bridge.main(threadCount = DEFAULT_THREAD_COUNT)
            }

            launch(engineContext) {
                while (isActive) {
                    val output = bridge.readLine() ?: continue
                    if (output.startsWith(INIT_TOKEN)) {
                        moveToState(State.Ready)
                    } else if (output.startsWith(BEST_MOVE_TOKEN)) {
                        // 0:bestmove 1:[e2e4] 2:ponder 3:a6a7
                        val move = output.split(" ")[1].trim()
                        assertStateOrNull<State.Moving>()?.completable?.complete(move)
                    }
                }
            }
        }.let { job ->
            try {
                awaitCancellation()
            } finally {
                bridge.writeLine(EngineCommand.Quit.toString())
                job.cancel()
                moveToState(State.Uninitialized)
            }
        }
    }

    override suspend fun getMove(params: FenAndDepth): String {
        val (fen, depth) = params
        return withContext(engineContext) {
            awaitState<State.Ready>()
            val moveCompletable = CompletableDeferred<String>()
            moveToState(State.Moving(moveCompletable))

            listOf(
                EngineCommand.SetPosition(fen),
                EngineCommand.GoDepth(depth),
            ).forEach {
                bridge.writeLine(it.toString())
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
        Timber.tag("Stockfish").d("Awaiting ${T::class.simpleName}")
        stateFlow.takeWhile { it !is T }.collect()
    }

    private suspend fun moveToState(state: State) {
        stateFlow.emit(state)
        Timber.tag("Stockfish").d("Moved to ${state::class.simpleName}")
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
        internal const val INIT_TOKEN = "Stockfish"
        internal const val BEST_MOVE_TOKEN = "bestmove"
        private const val DEFAULT_THREAD_COUNT = 4
    }
}
