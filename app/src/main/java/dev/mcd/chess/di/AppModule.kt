package dev.mcd.chess.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.BuildConfig
import dev.mcd.chess.data.api.ChessApiImpl
import dev.mcd.chess.data.game.BoardSoundsImpl
import dev.mcd.chess.data.game.online.JoinOnlineGameImpl
import dev.mcd.chess.data.prefs.AppPreferencesImpl
import dev.mcd.chess.data.stockfish.StockfishAdapter
import dev.mcd.chess.data.stockfish.StockfishAdapterImpl
import dev.mcd.chess.data.stockfish.StockfishJni
import dev.mcd.chess.domain.Environment
import dev.mcd.chess.domain.api.ChessApi
import dev.mcd.chess.domain.game.BoardSounds
import dev.mcd.chess.domain.game.local.GameSessionRepository
import dev.mcd.chess.domain.game.local.GameSessionRepositoryImpl
import dev.mcd.chess.domain.game.online.JoinOnlineGame
import dev.mcd.chess.domain.prefs.AppPreferences
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
    abstract fun appPrefs(impl: AppPreferencesImpl): AppPreferences

    companion object {
        @Provides
        @Singleton
        fun environment(appPreferences: AppPreferences): Environment {
            return runBlocking {
                if (BuildConfig.DEBUG) {
                    Environment.Debug(apiUrl = appPreferences.host())
                } else {
                    Environment.Production
                }
            }
        }

        @Provides
        @Singleton
        fun stockfishAdapter(): StockfishAdapter {
            return StockfishAdapterImpl(
                bridge = StockfishJni(),
                coroutineContext = CoroutineName("Stockfish") + Dispatchers.IO,
            )
        }
    }
}
