package dev.mcd.chess.api.data

import dev.mcd.chess.ChessApi
import dev.mcd.chess.OnlineGameChannel
import dev.mcd.chess.api.domain.AuthResponse
import dev.mcd.chess.api.domain.GameMessage
import dev.mcd.chess.api.domain.LobbyInfo
import dev.mcd.chess.api.serializer.AuthResponseSerializer
import dev.mcd.chess.api.serializer.GameStateMessageSerializer
import dev.mcd.chess.api.serializer.LobbyInfoSerializer
import dev.mcd.chess.api.serializer.domain
import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.game.online.GameSession
import dev.mcd.chess.common.player.UserId
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChessApiImpl constructor(
    private val apiUrl: String,
    private val client: HttpClient,
) : ChessApi {

    private val websocketUrl = apiUrl.let {
        if (it.contains("https")) {
            it.replace("https", "wss")
        } else {
            it.replace("http", "ws")
        }
    }

    override suspend fun generateId(): AuthResponse {
        return withContext(Dispatchers.IO) {
            client.post {
                url("$apiUrl/generate_id")
            }.body<AuthResponseSerializer>().domain()
        }
    }

    override suspend fun findGame(
        authToken: String,
    ): GameSession {
        return withContext(Dispatchers.IO) {
            val sessionCompletable = CompletableDeferred<GameSession>()

            client.webSocket(
                urlString = "$websocketUrl/game/find",
                request = {
                    bearerAuth(authToken)
                }
            ) {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val message = frame.gameMessage()
                        if (message is GameMessage.GameState) {
                            sessionCompletable.complete(message.session)
                            close()
                        } else {
                            // TODO: Timber.w("Unhandled message: ${message::class}")
                        }
                    }
                }
                val reason = closeReason.await()
                if (!sessionCompletable.isCompleted) {
                    println("No game joined: $reason")
                    sessionCompletable.cancel()
                }
            }
            sessionCompletable.await()
        }
    }

    override suspend fun game(
        authToken: String,
        id: GameId,
    ): GameSession {
        return withContext(Dispatchers.IO) {
            client.get {
                url("$apiUrl/game/id/$id")
                bearerAuth(authToken)
            }.body<GameStateMessageSerializer>().domain()
        }
    }

    override suspend fun gameForUser(
        authToken: String,
    ): List<GameSession> {
        return withContext(Dispatchers.IO) {
            client.get {
                url("$apiUrl/game/user")
                bearerAuth(authToken)
            }.body<List<GameStateMessageSerializer>>().map { it.domain() }
        }
    }

    override suspend fun joinGame(
        authToken: String,
        id: GameId,
        block: suspend OnlineGameChannel.() -> Unit
    ) {
        withContext(Dispatchers.IO) {
            client.webSocket(
                urlString = "$websocketUrl/game/join/$id",
                request = {
                    bearerAuth(authToken)
                }
            ) {
                runCatching {
                    val incomingMessages = Channel<GameMessage>(1, BufferOverflow.DROP_OLDEST)
                    val outgoing = Channel<String>(1, BufferOverflow.DROP_OLDEST)

                    launch {
                        runCatching {
                            block(OnlineGameChannel(incomingMessages, outgoing))
                        }.onFailure { println(it) }
                    }

                    launch {
                        runCatching {
                            for (command in outgoing) {
                                println("Sending $command")
                                send(Frame.Text(command))
                            }
                        }.onFailure { println(it) }
                    }

                    for (frame in incoming) {
                        if (frame !is Frame.Text) continue
                        try {
                            incomingMessages.send(frame.gameMessage())
                        } catch (e: Exception) {
                            println("Handling frame: $e")
                        }
                    }
                }.onFailure { println(it) }
            }
        }
    }

    override suspend fun lobbyInfo(excludeUser: UserId?): LobbyInfo {
        return withContext(Dispatchers.IO) {
            client.get {
                url("$apiUrl/game/lobby")
                excludeUser?.let {
                    parameter("excludeUser", excludeUser)
                }
            }.body<LobbyInfoSerializer>().domain()
        }
    }
}
