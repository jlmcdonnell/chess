package dev.mcd.chess.engine.stockfish

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.engine.stockfish.data.AndroidStockfishJni
import dev.mcd.chess.engine.stockfish.data.StockfishEngine
import dev.mcd.chess.engine.stockfish.data.StockfishJni
import dev.mcd.chess.common.engine.ChessEngine
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
