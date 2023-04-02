package dev.mcd.chess.feature.common

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.BuildConfig
import dev.mcd.chess.feature.common.data.AppPreferencesImpl
import dev.mcd.chess.feature.common.domain.AppPreferences
import dev.mcd.chess.feature.common.domain.Environment
import dev.mcd.chess.online.domain.AuthStore
import dev.mcd.chess.online.domain.EndpointProvider
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

        @Provides
        @Singleton
        fun endpointProvider(environment: Environment): EndpointProvider = object : EndpointProvider {
            override fun invoke() = environment.apiUrl
        }

        @Provides
        @Singleton
        fun authStore(appPreferences: AppPreferences): AuthStore = appPreferences
    }
}
