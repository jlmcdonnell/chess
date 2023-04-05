package dev.mcd.chess.app

import android.app.Application
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.common.engine.ChessEngine
import dev.mcd.chess.engine.lc0.Lc0
import dev.mcd.chess.engine.stockfish.Stockfish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface ChessEngineProvider {

    @Stockfish
    fun stockfish(): ChessEngine

    @Lc0
    fun lc0(): ChessEngine
}

context(Application)
fun initChessEngine() {
    val entryPoint = EntryPointAccessors.fromApplication(applicationContext, ChessEngineProvider::class.java)
    val lc0 = entryPoint.lc0()
    val stockfish = entryPoint.stockfish()

    CoroutineScope(Dispatchers.Default).launch {
        lc0.init()
        stockfish.init()
    }
}
