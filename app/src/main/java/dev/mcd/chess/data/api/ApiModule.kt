package dev.mcd.chess.data.api

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.domain.Environment
import dev.mcd.chess.domain.api.ChessApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import timber.log.Timber
import javax.inject.Singleton
import io.ktor.client.plugins.logging.Logger as KtorLogger

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun chessApi(
        environment: Environment,
        apiCredentialsStore: ApiCredentialsStore,
        client: HttpClient,
    ): ChessApi = ChessApiImpl(
        apiUrl = environment.apiUrl,
        client = client,
        apiCredentialsStore = apiCredentialsStore,
    )

    @Provides
    @Singleton
    fun apiCredentialsStore(@ApplicationContext context: Context): ApiCredentialsStore {
        return ApiCredentialsStoreImpl(context)
    }

    @Provides
    @Singleton
    fun ktorLogger(): KtorLogger {
        return object : KtorLogger {
            override fun log(message: String) {
                Timber.tag("Ktor").d(message)
            }
        }
    }

    @Provides
    @Singleton
    fun httpClient(logger: KtorLogger): HttpClient {
        return HttpClient(OkHttp) {
            install(WebSockets) {
                pingInterval = 1500
            }
            install(ContentNegotiation) {
                json()
            }
            install(HttpTimeout)
            install(Logging) {
                this.logger = logger
                this.level = LogLevel.BODY
            }
        }
    }
}
