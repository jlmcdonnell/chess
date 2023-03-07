package dev.mcd.chess.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.data.api.ChessApiImpl
import dev.mcd.chess.data.api.DebugHostStoreImpl
import dev.mcd.chess.domain.game.online.OnlineGameChannel
import dev.mcd.chess.data.game.BoardSoundsImpl
import dev.mcd.chess.data.game.online.JoinOnlineGameImpl
import dev.mcd.chess.data.stockfish.StockfishAdapter
import dev.mcd.chess.data.stockfish.StockfishAdapterImpl
import dev.mcd.chess.data.stockfish.StockfishJni
import dev.mcd.chess.domain.Environment
import dev.mcd.chess.domain.api.ChessApi
import dev.mcd.chess.domain.api.DebugHostStore
import dev.mcd.chess.domain.game.BoardSounds
import dev.mcd.chess.domain.game.local.GameSessionRepository
import dev.mcd.chess.domain.game.local.GameSessionRepositoryImpl
import dev.mcd.chess.domain.game.online.JoinOnlineGame
import io.ktor.client.plugins.logging.Logger
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun gameSessionRepo(impl: GameSessionRepositoryImpl): GameSessionRepository

    @Binds
    @Singleton
    abstract fun boardSounds(impl: BoardSoundsImpl): BoardSounds

    @Binds
    @Singleton
    abstract fun debugHostStore(impl: DebugHostStoreImpl): DebugHostStore

    @Binds
    abstract fun joinOnlineGame(impl: JoinOnlineGameImpl): JoinOnlineGame

    companion object {
        @Provides
        @Singleton
        fun environment(debugHostStore: DebugHostStore): Environment {
            return runBlocking {
                Environment.Debug(apiUrl = debugHostStore.host())
            }
        }

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
            logger: Logger,
        ): ChessApi = ChessApiImpl(
            context = context,
            apiUrl = environment.apiUrl,
            logger = logger,
        )

        @Provides
        @Singleton
        fun ktorLogger(): Logger {
            return object : Logger {
                override fun log(message: String) {
                    Timber.tag("Ktor").d(message)
                }
            }
        }
    }
}
