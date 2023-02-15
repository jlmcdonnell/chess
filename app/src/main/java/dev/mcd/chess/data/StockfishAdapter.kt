package dev.mcd.chess.data

import dev.mcd.chess.jni.StockfishJni
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

interface StockfishAdapter {
    fun start(context: CoroutineContext): Job
    suspend fun getMove(fen: String, level: Int, depth: Int): String
    suspend fun quit()
}

class StockfishAdapterImpl(
    private val bridge: StockfishJni,
) : StockfishAdapter {

    private val stateFlow = MutableStateFlow<State>(State.Uninitialized)

    override fun start(context: CoroutineContext): Job {
        return CoroutineScope(context).launch(Dispatchers.IO) {
            val bridge = StockfishJni()
            val readyCompletable = CompletableDeferred<Unit>()
            bridge.init()

            if (DEBUG) Timber.d("STOCKFISH INIT")

            launch {
                bridge.main()
            }
            launch {
                while (isActive) {
                    val output = bridge.readLine()

                    if (DEBUG) println("STOCKFISH: $output")

                    if (output.startsWith("Stockfish")) {
                        readyCompletable.complete(Unit)
                    } else if (output.startsWith("bestmove")) {
                        val move = output.split(" ")[1].trim() // 0:bestmove 1:[e2e4] 2:ponder 3:a6a7
                        assertStateOrNull<State.Moving>()?.completable?.complete(move)
                    } else if (output.startsWith("quitok")) {
                        assertStateOrNull<State.Quitting>()?.completable?.complete(Unit)
                    }
                }
            }
            readyCompletable.await()
            stateFlow.emit(State.Ready)
        }
    }

    override suspend fun getMove(fen: String, level: Int, depth: Int): String {
        return withContext(Dispatchers.IO) {
            assertState<State.Ready>()
            val moveCompletable = CompletableDeferred<String>()
            stateFlow.emit(State.Moving(moveCompletable))

            bridge.writeLn("setoption name Skill Level value $level")
            bridge.writeLn("position fen $fen")
            bridge.writeLn("go depth $depth")

            moveCompletable.await().also {
                stateFlow.emit(State.Ready)
            }
        }
    }

    override suspend fun quit() {
        withContext(Dispatchers.IO) {
            when (val state = stateFlow.value) {
                is State.Uninitialized -> return@withContext
                is State.Quitting -> {
                    state.completable.await()
                    return@withContext
                }
                is State.Moving,
                is State.Ready -> Unit
            }

            val completable = CompletableDeferred<Unit>()
            stateFlow.emit(State.Quitting(completable))
            bridge.writeLn("quit")
            completable.await()
        }
    }

    private inline fun <reified T : State> assertStateOrNull(): T? {
        return stateFlow.value as? T
    }

    private inline fun <reified T : State> assertState(): T {
        return stateFlow.value as? T ?: throw Exception("Expected ${T::class.simpleName} but was ${stateFlow.value::class.simpleName}")
    }

    private sealed interface State {
        object Uninitialized : State

        class Moving(
            val completable: CompletableDeferred<String>,
        ) : State

        class Quitting(
            val completable: CompletableDeferred<Unit>
        ) : State

        object Ready : State
    }

    companion object {
        private const val DEBUG = true
    }

}
