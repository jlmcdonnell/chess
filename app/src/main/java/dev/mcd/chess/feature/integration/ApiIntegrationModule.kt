package dev.mcd.chess.feature.integration

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.ChessApi
import dev.mcd.chess.api.data.ChessApiImpl
import dev.mcd.chess.feature.common.domain.Environment
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiIntegrationModule {
    @Provides
    @Singleton
    fun chessApi(
        environment: Environment,
        client: HttpClient,
    ): ChessApi = ChessApiImpl(
        apiUrl = environment.apiUrl,
        client = client,
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

    @Provides
    @Singleton
    fun httpClient(logger: Logger): HttpClient {
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
