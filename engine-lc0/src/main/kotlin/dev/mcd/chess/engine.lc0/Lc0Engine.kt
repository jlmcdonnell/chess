package dev.mcd.chess.engine.lc0

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.mcd.chess.common.engine.ChessEngine
import dev.mcd.chess.common.engine.EngineCommand
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class Lc0Engine @Inject constructor(
    private val bridge: Lc0Jni,
    @ApplicationContext
    private val context: Context,
    private val engineContext: CoroutineContext,
) : ChessEngine {

    private val weightsPath = File(context.dataDir, "maia-1100.pb").absolutePath

    override fun init() {
        context.assets.open("maia-1100.pb").copyTo(
            File(context.dataDir, "maia-1100.pb").outputStream(),
        )

        bridge.init()
    }

    override suspend fun awaitReady() {
    }

    override suspend fun startAndWait() {
        println("startAndWait")
        withContext(engineContext) {
            launch {
                println("main")
                bridge.main(weightsPath)
                println("main done")
            }
            launch {
                while (true) {
                    println("Lc0EngineKt OUT: ${bridge.readLine()}")
                }
            }
            launch {
                while (true) {
                    println("Lc0EngineKt ERR: ${bridge.readError()}")
                }
            }

            awaitCancellation()
        }
    }

    override suspend fun getMove(fen: String, level: Int, depth: Int): String {
        withContext(engineContext) {
            listOf(
                EngineCommand.SetPosition(fen),
                EngineCommand.Go(depth),
            ).forEach {
                bridge.writeLine(it.string())
            }
        }
        return "e2e4"
    }
}
