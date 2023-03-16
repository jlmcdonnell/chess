package dev.mcd.chess.engine.data

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

class StockfishAdapterImplTest {

    private lateinit var bridge: StockfishJni
    private lateinit var adapter: StockfishAdapterImpl

    private val coroutineContext = Dispatchers.IO

    @Before
    fun setUp() {
        bridge = mockk(relaxUnitFun = true)
        adapter = StockfishAdapterImpl(bridge, coroutineContext)
    }

    @Test
    fun `Start and emit ready`() = runBlocking {
        val complete = CompletableDeferred<Unit>()

        every { bridge.readLine() } returns "Stockfish" andThenJust Awaits
        every { bridge.main() } coAnswers {
            complete.complete(Unit)
        }

        CoroutineScope(Dispatchers.Default).launch {
            adapter.startAndWait()
        }

        complete.await()

        verify(exactly = 1) { bridge.main() }
    }

    @Test
    fun `Get move`(): Unit = runBlocking {
        val move = CompletableDeferred<String>()

        every { bridge.writeLn("position fen TEST") } returns Unit
        every { bridge.writeLn("setoption name Skill Level value 0") } returns Unit
        every { bridge.writeLn("go depth 0") } coAnswers {
            move.complete("bestmove e2e4")
            Unit
        }

        every { bridge.readLine() } returns "Stockfish" coAndThen {
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
