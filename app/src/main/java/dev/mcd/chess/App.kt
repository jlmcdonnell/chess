package dev.mcd.chess

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.mcd.chess.common.engine.ChessEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var engine: ChessEngine

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        CoroutineScope(Dispatchers.Default).launch {
            engine.init()
        }
    }
}
