package dev.mcd.chess.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.data.BoardSoundsImpl
import dev.mcd.chess.data.stockfish.StockfishAdapter
import dev.mcd.chess.data.stockfish.StockfishAdapterImpl
import dev.mcd.chess.data.stockfish.StockfishJni
import dev.mcd.chess.domain.BoardSounds
import dev.mcd.chess.domain.GameSessionRepository
import dev.mcd.chess.domain.GameSessionRepositoryImpl
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun gameSessionRepo(impl: GameSessionRepositoryImpl): GameSessionRepository

    @Binds
    @Singleton
    abstract fun boardSounds(impL: BoardSoundsImpl): BoardSounds

    companion object {
        @Provides
        @Singleton
        fun stockfishJni() = StockfishJni()

        @Provides
        @Singleton
        fun stockfishAdapter(stockfishJni: StockfishJni): StockfishAdapter {
            return StockfishAdapterImpl(
                bridge = stockfishJni,
                coroutineContext = CoroutineName("Stockfish") + Dispatchers.IO,
            )
        }
    }
}
