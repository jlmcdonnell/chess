package dev.mcd.chess.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.BuildConfig
import dev.mcd.chess.game.data.BoardSoundsImpl
import dev.mcd.chess.common.data.AppPreferencesImpl
import dev.mcd.chess.common.domain.Environment
import dev.mcd.chess.game.domain.BoardSounds
import dev.mcd.chess.game.domain.GameSessionRepository
import dev.mcd.chess.game.data.GameSessionRepositoryImpl
import dev.mcd.chess.common.domain.AppPreferences
import kotlinx.coroutines.runBlocking
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
    }
}
