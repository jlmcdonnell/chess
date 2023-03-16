package dev.mcd.chess.engine.data

import dev.mcd.chess.engine.domain.ChessEngine
import dev.mcd.chess.engine.domain.EngineCommand
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class StockfishEngine(
    private val bridge: StockfishJni,
    private val context: CoroutineContext,
) : ChessEngine {

    private val stateFlow = MutableStateFlow<State>(State.Uninitialized)

    override suspend fun startAndWait() {
        withContext(context) {
            val readyCompletable = CompletableDeferred<Unit>()

            launch(context) {
                bridge.main()
            }

            launch(context) {
                while (true) {
                    val output = bridge.readLine()

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
