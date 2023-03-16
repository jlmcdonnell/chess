package dev.mcd.chess.engine

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.engine.data.AndroidStockfishJni
import dev.mcd.chess.engine.data.StockfishEngine
import dev.mcd.chess.engine.data.StockfishJni
import dev.mcd.chess.engine.domain.ChessEngine
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class StockfishModule {
    @Provides
    @Singleton
    fun stockfishJni(): StockfishJni = AndroidStockfishJni()

    @Provides
    @Singleton
    fun stockfishEngine(bridge: StockfishJni): ChessEngine {
        val context = CoroutineName("Stockfish") + Dispatchers.IO
        return StockfishEngine(bridge, context)
    }
}
