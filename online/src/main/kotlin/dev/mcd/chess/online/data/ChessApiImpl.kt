package dev.mcd.chess.online.data

import dev.mcd.chess.common.game.GameId
import dev.mcd.chess.common.player.UserId
import dev.mcd.chess.online.data.mapper.gameMessage
import dev.mcd.chess.online.data.serializer.AuthResponseSerializer
import dev.mcd.chess.online.data.serializer.GameStateMessageSerializer
import dev.mcd.chess.online.data.serializer.LobbyInfoSerializer
import dev.mcd.chess.online.data.serializer.domain
import dev.mcd.chess.online.domain.ChessApi
import dev.mcd.chess.online.domain.OnlineGameChannel
import dev.mcd.chess.online.domain.entity.AuthResponse
import dev.mcd.chess.online.domain.entity.GameMessage
import dev.mcd.chess.online.domain.entity.LobbyInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
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

internal class ChessApiImpl constructor(
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
    ): GameId {
        return withContext(Dispatchers.IO) {
            val sessionCompletable = CompletableDeferred<GameId>()

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
                            sessionCompletable.complete(message.id)
                            close()
                        } else {
                             throw Exception("Unhandled message: ${message::class}")
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


    override suspend fun gameForUser(
        authToken: String,
    ): List<GameId> {
        return withContext(Dispatchers.IO) {
            client.get {
                url("$apiUrl/game/user")
                bearerAuth(authToken)
            }.body<List<GameStateMessageSerializer>>().map { it.domain().id }
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
