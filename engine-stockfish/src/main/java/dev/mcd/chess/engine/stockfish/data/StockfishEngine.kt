package dev.mcd.chess.engine.stockfish.data

import dev.mcd.chess.common.engine.ChessEngine
import dev.mcd.chess.common.engine.EngineCommand
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

internal class StockfishEngine(
    private val bridge: StockfishJni,
    private val context: CoroutineContext,
) : ChessEngine {

    private val stateFlow = MutableStateFlow<State>(State.Uninitialized)

    override suspend fun load() {
        withContext(context) {
            bridge.init()
        }
    }

    override suspend fun awaitReady() {
        Timber.tag("Stockfish").d("Awaiting ready")
        stateFlow.takeWhile { it != State.Ready }.collect()
        Timber.tag("Stockfish").d("Ready")
    }

    override suspend fun startAndWait() {
        withContext(context) {
            val readyCompletable = CompletableDeferred<Unit>()

            launch(context, start = CoroutineStart.UNDISPATCHED) {
                yield()
                bridge.main()
            }

            launch {
                while (true) {
                    val output = bridge.readLine()
                    Timber.tag("Stockfish").d("Stockfish Output: $output")
                    if (output.startsWith(INIT_TOKEN)) {
                        readyCompletable.complete(Unit)
                    } else if (output.startsWith(BEST_MOVE_TOKEN)) {
                        // 0:bestmove 1:[e2e4] 2:ponder 3:a6a7
                        val move = output.split(" ")[1].trim()
                        assertStateOrNull<State.Moving>()?.completable?.complete(move)
                    }
                }
            }

            readyCompletable.await()
            stateFlow.emit(State.Ready)
            awaitCancellation()
        }
    }

    override suspend fun getMove(fen: String, level: Int, depth: Int): String {
        return withContext(context) {
            assertState<State.Ready>()
            val moveCompletable = CompletableDeferred<String>()
            stateFlow.emit(State.Moving(moveCompletable))

            listOf(
                EngineCommand.SetSkillLevel(level),
                EngineCommand.SetPosition(fen),
                EngineCommand.Go(depth),
            ).forEach {
                bridge.writeLn(it.string())
            }

            moveCompletable.await().also {
                stateFlow.emit(State.Ready)
            }
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

        object Ready : State
    }

    companion object {
        internal const val INIT_TOKEN = "Stockfish"
        internal const val BEST_MOVE_TOKEN = "bestmove"
    }
}