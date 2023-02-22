package dev.mcd.chess.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.data.BoardSoundsImpl
import dev.mcd.chess.data.api.ChessApiImpl
import dev.mcd.chess.data.stockfish.StockfishAdapter
import dev.mcd.chess.data.stockfish.StockfishAdapterImpl
import dev.mcd.chess.data.stockfish.StockfishJni
import dev.mcd.chess.domain.Environment
import dev.mcd.chess.domain.api.ChessApi
import dev.mcd.chess.domain.game.BoardSounds
import dev.mcd.chess.domain.game.GameSessionRepository
import dev.mcd.chess.domain.game.GameSessionRepositoryImpl
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
        fun environment(): Environment = Environment.Debug(
            apiHost = "192.168.1.159",
            apiPort = 8080,
            apiScheme = "http",
        )

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

        @Provides
        @Singleton
        fun chessApi(
            @ApplicationContext context: Context,
            environment: Environment,
        ): ChessApi = ChessApiImpl(
            context = context,
            apiHost = environment.apiHost,
            apiScheme = environment.apiScheme,
            apiPort = environment.apiPort,
        )
    }
}
