package dev.mcd.chess.data.stockfish

import dev.mcd.chess.jni.StockfishJni
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

interface StockfishAdapter {
    suspend fun start()
    suspend fun getMove(fen: String, level: Int, depth: Int): String
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

                    Timber.d("STOCKFISH: $output")

                    if (output.startsWith("Stockfish")) {
                        readyCompletable.complete(Unit)
                    } else if (output.startsWith("bestmove")) {
                        // 0:bestmove 1:[e2e4] 2:ponder 3:a6a7
                        val move = output.split(" ")[1].trim()
                        assertStateOrNull<State.Moving>()?.completable?.complete(move)
                    }
                }
            }
            readyCompletable.await()
            stateFlow.emit(State.Ready)
            println("JOOIN")
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

        object Ready : State
    }
}
