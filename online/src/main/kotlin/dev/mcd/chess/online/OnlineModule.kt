package dev.mcd.chess.online

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.online.data.ChessApiImpl
import dev.mcd.chess.online.data.usecase.FindGameImpl
import dev.mcd.chess.online.data.usecase.GetGameForUserImpl
import dev.mcd.chess.online.data.usecase.GetLobbyInfoImpl
import dev.mcd.chess.online.data.usecase.GetOrCreateUserImpl
import dev.mcd.chess.online.data.usecase.JoinOnlineGameImpl
import dev.mcd.chess.online.domain.ChessApi
import dev.mcd.chess.online.domain.EndpointProvider
import dev.mcd.chess.online.domain.usecase.FindGame
import dev.mcd.chess.online.domain.usecase.GetGameForUser
import dev.mcd.chess.online.domain.usecase.GetLobbyInfo
import dev.mcd.chess.online.domain.usecase.GetOrCreateUser
import dev.mcd.chess.online.domain.usecase.JoinOnlineGame
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import javax.inject.Singleton
import io.ktor.client.plugins.logging.Logger as KtorLogger

@Module
@InstallIn(SingletonComponent::class)
abstract class OnlineModule {

    @Binds
    internal abstract fun joinOnlineGame(impl: JoinOnlineGameImpl): JoinOnlineGame

    @Binds
    internal abstract fun getOrCreateUser(impl: GetOrCreateUserImpl): GetOrCreateUser

    @Binds
    internal abstract fun findGame(impl: FindGameImpl): FindGame

    @Binds
    internal abstract fun getGameForUser(impl: GetGameForUserImpl): GetGameForUser

    @Binds
    internal abstract fun getLobbyInfo(impl: GetLobbyInfoImpl): GetLobbyInfo

    companion object {
        @Provides
        @Singleton
        fun chessApi(
            endpointProvider: EndpointProvider,
            client: HttpClient,
        ): ChessApi = ChessApiImpl(
            apiUrl = endpointProvider(),
            client = client,
        )

        @Provides
        @Singleton
        fun ktorLogger(): KtorLogger {
            return object : KtorLogger {
                override fun log(message: String) {
                    println(message)
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
}
