package dev.mcd.chess.app

import android.app.Application
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.common.engine.ChessEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface ChessEngineProvider {
    fun engine(): ChessEngine
}

context(Application)
fun initChessEngine() {
    val engine = EntryPointAccessors.fromApplication(applicationContext, ChessEngineProvider::class.java).engine()
    CoroutineScope(Dispatchers.Default).launch {
        engine.init()
    }
}
