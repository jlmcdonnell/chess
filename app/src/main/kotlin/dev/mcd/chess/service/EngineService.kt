package dev.mcd.chess.service

import android.app.Service
import android.content.Intent
import android.os.Process
import dev.mcd.chess.common.engine.ChessEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class EngineService<EngineInit, Move, Engine : ChessEngine<EngineInit, Move>> : Service() {

    private var engineJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        engineJob = CoroutineScope(Dispatchers.Default).launch {
            with(engine()) {
                init(initParams())
                startAndWait()
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

    abstract suspend fun initParams(): EngineInit

    abstract fun engine(): ChessEngine<EngineInit, Move>
}
