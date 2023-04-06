package dev.mcd.chess.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Process
import dev.mcd.chess.common.engine.ChessEngine
import dev.mcd.chess.engine.EngineInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class EngineService : Service() {

    private var engineJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        engineJob = CoroutineScope(Dispatchers.Default).launch {
            with(engine()) {
                init()
                startAndWait()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return object : EngineInterface.Stub() {
            override fun bestMove(fen: String, depth: Int): String {
                return runBlocking {
                    engine().getMove(fen, depth)
                }
            }
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        stopSelf()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy")
        Process.killProcess(Process.myPid())
        engineJob?.cancel()
    }

    abstract fun engine(): ChessEngine

    companion object {
        inline fun <reified T : EngineService> newIntent(context: Context): Intent {
            return Intent(context, T::class.java)
        }
    }
}
