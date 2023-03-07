package dev.mcd.chess.data.api

import dev.mcd.chess.data.api.serializer.GameStateMessageSerializer
import dev.mcd.chess.data.api.serializer.LobbyInfoSerializer
import dev.mcd.chess.data.api.serializer.domain
import dev.mcd.chess.domain.api.ChessApi
import dev.mcd.chess.domain.api.LobbyInfo
import dev.mcd.chess.domain.game.GameId
import dev.mcd.chess.domain.game.GameMessage
import dev.mcd.chess.domain.game.online.GameSession
import dev.mcd.chess.domain.game.online.OnlineGameChannel
import dev.mcd.chess.domain.player.UserId
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject


class ChessApiImpl @Inject constructor(
    private val apiUrl: String,
    private val client: HttpClient,
    private val apiCredentialsStore: ApiCredentialsStore,
) : ChessApi {

    private val websocketUrl = apiUrl.let {
        if (it.contains("https")) {
            it.replace("https", "wss")
        } else {
            it.replace("http", "ws")
        }
    }

    override suspend fun generateId(): UserId {
        return withContext(Dispatchers.IO) {
            client.post {
                url("$apiUrl/generate_id")
            }.body<AuthResponse>().let { response ->
                apiCredentialsStore.apply {
                    storeToken(response.token)
                    storeUserId(response.userId)
                }
                response.userId
            }
        }
    }

    override suspend fun findGame(): GameSession {
        return withContext(Dispatchers.IO) {
            val sessionCompletable = CompletableDeferred<GameSession>()
            val token = requireNotNull(apiCredentialsStore.token()) { "No auth token" }

            client.webSocket(
                urlString = "$websocketUrl/game/find",
                request = {
                    bearerAuth(token)
                }
            ) {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val message = frame.gameMessage()
                        if (message is GameMessage.GameState) {
                            sessionCompletable.complete(message.session)
                            close()
                        } else {
                            Timber.w("Unhandled message: ${message::class}")
                        }
                    }
                }
                val reason = closeReason.await()
                if (!sessionCompletable.isCompleted) {
                    Timber.d("No game joined: $reason")
                    sessionCompletable.cancel()
                }
            }
            sessionCompletable.await()
        }
    }

    override suspend fun game(id: GameId): GameSession {
        return withContext(Dispatchers.IO) {
            client.get {
                url("$apiUrl/game/id/$id")
                withBearerToken()
            }.body<GameStateMessageSerializer>().domain()
        }
    }

    override suspend fun gameForUser(): List<GameSession> {
        return withContext(Dispatchers.IO) {
            client.get {
                url("$apiUrl/game/user")
                withBearerToken()
            }.body<List<GameStateMessageSerializer>>().map { it.domain() }
        }
    }

    override suspend fun joinGame(id: GameId, block: suspend OnlineGameChannel.() -> Unit) {
        withContext(Dispatchers.IO) {
            val token = requireNotNull(apiCredentialsStore.token()) { "No auth token" }

            client.webSocket(
                urlString = "$websocketUrl/game/join/$id",
                request = {
                    bearerAuth(token)
                }
            ) {
                runCatching {
                    val incomingMessages = Channel<GameMessage>(1, BufferOverflow.DROP_OLDEST)
                    val outgoing = Channel<String>(1, BufferOverflow.DROP_OLDEST)

                    launch {
                        runCatching {
                            block(OnlineGameChannel(incomingMessages, outgoing))
                        }.onFailure { Timber.e(it) }
                    }

                    launch {
                        runCatching {
                            for (command in outgoing) {
                                Timber.d("Sending $command")
                                send(Frame.Text(command))
                            }
                        }.onFailure { Timber.e(it) }
                    }

                    for (frame in incoming) {
                        if (frame !is Frame.Text) continue
                        try {
                            incomingMessages.send(frame.gameMessage())
                        } catch (e: Exception) {
                            Timber.e(e, "Handling frame")
                        }
                    }
                }.onFailure { Timber.e(it) }
            }
        }
    }

    override suspend fun lobbyInfo(): LobbyInfo {
        return withContext(Dispatchers.IO) {
            client.get {
                url("$apiUrl/game/lobby")
            }.body<LobbyInfoSerializer>().domain()
        }
    }

    private suspend fun HttpRequestBuilder.withBearerToken() {
        val token = requireNotNull(apiCredentialsStore.token()) { "No auth token" }
        bearerAuth(token)
    }


}
