package dev.mcd.chess.feature.common

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.BuildConfig
import dev.mcd.chess.feature.game.data.BoardSoundsImpl
import dev.mcd.chess.feature.common.data.AppPreferencesImpl
import dev.mcd.chess.feature.common.domain.Environment
import dev.mcd.chess.feature.game.domain.BoardSounds
import dev.mcd.chess.feature.game.domain.GameSessionRepository
import dev.mcd.chess.feature.game.data.GameSessionRepositoryImpl
import dev.mcd.chess.feature.common.domain.AppPreferences
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModule {

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
