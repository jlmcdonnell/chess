package dev.mcd.chess.data.stockfish

import dev.mcd.chess.jni.StockfishJni
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface StockfishAdapter {
    suspend fun start()
    suspend fun getMove(fen: String, level: Int, depth: Int): String
    suspend fun quit()
}

class StockfishAdapterImpl(
    private val bridge: StockfishJni,
    private val dispatcher: CoroutineDispatcher,
) : StockfishAdapter {

    private val stateFlow = MutableStateFlow<State>(State.Uninitialized)

    override suspend fun start() {
        withContext(dispatcher) {
            val bridge = StockfishJni()
            val readyCompletable = CompletableDeferred<Unit>()
            bridge.init()

            launch {
                bridge.main()
            }
            launch {
                while (isActive) {
                    val output = bridge.readLine()

                    println("STOCKFISH: $output")

                    if (output.startsWith("Stockfish")) {
                        readyCompletable.complete(Unit)
                    } else if (output.startsWith("bestmove")) {
                        val move =
                            output.split(" ")[1].trim() // 0:bestmove 1:[e2e4] 2:ponder 3:a6a7
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
        return stateFlow.value as? T
            ?: throw Exception("Expected ${T::class.simpleName} but was ${stateFlow.value::class.simpleName}")
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
}
