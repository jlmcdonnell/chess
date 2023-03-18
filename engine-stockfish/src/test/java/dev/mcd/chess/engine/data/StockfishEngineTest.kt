package dev.mcd.chess.engine.data

import dev.mcd.chess.common.engine.EngineCommand.Go
import dev.mcd.chess.common.engine.EngineCommand.SetPosition
import dev.mcd.chess.common.engine.EngineCommand.SetSkillLevel
import dev.mcd.chess.engine.stockfish.data.StockfishEngine
import dev.mcd.chess.engine.stockfish.data.StockfishJni
import io.mockk.Awaits
import io.mockk.andThenJust
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class StockfishEngineTest {

    private lateinit var bridge: StockfishJni
    private lateinit var adapter: StockfishEngine

    private val coroutineContext = Dispatchers.IO

    @Before
    fun setUp() {
        bridge = mockk(relaxUnitFun = true)
        adapter = StockfishEngine(bridge, coroutineContext)
    }

    @Test
    fun `Start and emit ready`() = runBlocking {
        every { bridge.readLine() } returns "Stockfish" andThenJust Awaits
        every { bridge.main() } returns Unit

        CoroutineScope(Dispatchers.Default).launch {
            adapter.startAndWait()
        }

        adapter.awaitReady()

        verify(exactly = 1) { bridge.main() }
    }

    @Test
    fun `Get move`(): Unit = runBlocking {
        val move = CompletableDeferred<String>()

        every { bridge.writeLn(SetPosition("TEST").string()) } returns Unit
        every { bridge.writeLn(SetSkillLevel(0).string()) } returns Unit
        every { bridge.writeLn(Go(0).string()) } coAnswers {
            move.complete("${StockfishEngine.BEST_MOVE_TOKEN} e2e4")
            Unit
        }

        every { bridge.readLine() } returns StockfishEngine.INIT_TOKEN coAndThen {
            move.await()
        }
        every { bridge.main() } returns Unit

        CoroutineScope(Dispatchers.Default).launch {
            adapter.startAndWait()
        }

        println("getMove")
        adapter.getMove("TEST", 0, 0)
    }
}
