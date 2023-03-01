package dev.mcd.chess.data.api

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.mcd.chess.data.api.serializer.ActiveGameFactory
import dev.mcd.chess.data.api.serializer.LobbyInfoSerializer
import dev.mcd.chess.data.api.serializer.SessionInfoSerializer
import dev.mcd.chess.data.api.serializer.domain
import dev.mcd.chess.domain.api.ActiveGame
import dev.mcd.chess.domain.api.ChessApi
import dev.mcd.chess.domain.api.LobbyInfo
import dev.mcd.chess.domain.api.SessionInfo
import dev.mcd.chess.domain.game.GameMessage
import dev.mcd.chess.domain.game.SessionId
import dev.mcd.chess.domain.player.UserId
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "api-prefs")

class ChessApiImpl @Inject constructor(
    context: Context,
    private val apiUrl: String,
    private val activeGameFactory: ActiveGameFactory,
) : ChessApi {

    private val store = context.dataStore
    private val tokenKey = stringPreferencesKey("token")
    private val userKey = stringPreferencesKey("user")
    private val websocketUrl = apiUrl.let {
        if (it.contains("https")) {
            it.replace("https", "wss")
        } else {
            it.replace("http", "ws")
        }
    }

    private val client = HttpClient(OkHttp) {
        install(WebSockets) {
            pingInterval = 1500
        }
        install(ContentNegotiation) {
            json()
        }
        install(HttpTimeout)
    }

    override suspend fun generateId(): UserId {
        return withContext(Dispatchers.IO) {
            client.post {
                url("$apiUrl/generate_id")
            }.body<AuthResponse>().let { response ->
                storeToken(response.token)
                store.edit { it[userKey] = response.userId }
                response.userId
            }
        }
    }

    override suspend fun findGame(): SessionInfo {
        return withContext(Dispatchers.IO) {
            val sessionCompletable = CompletableDeferred<SessionInfo>()
            val token = requireNotNull(token()) { "No auth token" }

            client.webSocket(
                urlString = "$websocketUrl/game/find",
                request = {
                    bearerAuth(token)
                }
            ) {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val message = frame.gameMessage()
                        if (message is GameMessage.SessionInfoMessage) {
                            sessionCompletable.complete(message.sessionInfo)
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

    override suspend fun game(id: SessionId): SessionInfo {
        return withContext(Dispatchers.IO) {
            client.get {
                url("$apiUrl/game/id/$id")
                withBearerToken()
            }.body<SessionInfoSerializer>().domain()
        }
    }

    override suspend fun gameForUser(): List<SessionInfo> {
        return withContext(Dispatchers.IO) {
            client.get {
                url("$apiUrl/game/user")
                withBearerToken()
            }.body<List<SessionInfoSerializer>>().map { it.domain() }
        }
    }

    override suspend fun joinGame(id: SessionId, block: suspend ActiveGame.() -> Unit) {
        withContext(Dispatchers.IO) {
            val token = requireNotNull(token()) { "No auth token" }

            client.webSocket(
                urlString = "$websocketUrl/game/join/$id",
                request = {
                    bearerAuth(token)
                }
            ) {
                val incomingMessages = Channel<GameMessage>(1, BufferOverflow.DROP_OLDEST)
                val outgoing = Channel<String>(1, BufferOverflow.DROP_OLDEST)

                launch {
                    block(activeGameFactory(outgoing, incomingMessages))
                }

                launch {
                    for (command in outgoing) {
                        Timber.d("Sending $command")
                        send(Frame.Text(command))
                    }
                }

                for (frame in incoming) {
                    if (frame !is Frame.Text) continue
                    try {
                        incomingMessages.send(frame.gameMessage())
                    } catch (e: Exception) {
                        Timber.e(e, "Handling frame")
                    }
                }
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

    override suspend fun storeToken(token: String) {
        store.edit { it[tokenKey] = token }
    }

    override suspend fun userId(): UserId? {
        return store.data.first()[userKey]
    }

    override suspend fun clear() {
        store.edit { it.clear() }
    }

    private suspend fun HttpRequestBuilder.withBearerToken() {
        val token = requireNotNull(token()) { "No auth token" }
        bearerAuth(token)
    }

    private suspend fun token(): String? {
        return store.data.first()[tokenKey]
    }
}
