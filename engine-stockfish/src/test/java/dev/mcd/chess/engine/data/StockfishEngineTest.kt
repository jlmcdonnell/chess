package dev.mcd.chess.engine.data

import dev.mcd.chess.common.engine.EngineCommand.GoDepth
import dev.mcd.chess.common.engine.EngineCommand.SetPosition
import dev.mcd.chess.engine.stockfish.data.FenAndDepth
import dev.mcd.chess.engine.stockfish.data.StockfishEngine
import dev.mcd.chess.engine.stockfish.data.StockfishJni
import io.kotest.core.spec.style.StringSpec
import io.mockk.Awaits
import io.mockk.andThenJust
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StockfishEngineTest : StringSpec(
    {
        val coroutineContext = Dispatchers.IO
        lateinit var bridge: StockfishJni
        lateinit var adapter: StockfishEngine

        beforeTest {
            bridge = mockk(relaxUnitFun = true)
            adapter = StockfishEngine(bridge, coroutineContext)
        }

        "Start and emit ready" {
            every { bridge.readLine() } returns "Stockfish" andThenJust Awaits
            every { bridge.main(threadCount = 1) } returns Unit

            CoroutineScope(Dispatchers.Default).launch {
                adapter.startAndWait()
            }

            adapter.awaitReady()

            verify(exactly = 1) { bridge.main(threadCount = any()) }
        }

        "Get move" {
            val move = CompletableDeferred<String>()

            every { bridge.writeLine(SetPosition("TEST").toString()) } returns Unit
            every { bridge.writeLine(GoDepth(0).toString()) } coAnswers {
                move.complete("${StockfishEngine.BEST_MOVE_TOKEN} e2e4")
                Unit
            }

            every { bridge.readLine() } returns StockfishEngine.INIT_TOKEN coAndThen {
                move.await()
            }
            every { bridge.main(threadCount = any()) } returns Unit

            CoroutineScope(Dispatchers.Default).launch {
                adapter.startAndWait()
            }

            adapter.getMove(FenAndDepth("TEST", 0))
        }
    },
)
