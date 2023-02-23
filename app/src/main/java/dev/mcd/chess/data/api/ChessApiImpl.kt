package dev.mcd.chess.data.api

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.mcd.chess.data.api.serializer.GameMessageSerializer
import dev.mcd.chess.data.api.serializer.MessageType
import dev.mcd.chess.data.api.serializer.SessionInfoSerializer
import dev.mcd.chess.data.api.serializer.asSessionInfo
import dev.mcd.chess.data.api.serializer.domain
import dev.mcd.chess.domain.api.ActiveGame
import dev.mcd.chess.domain.api.ChessApi
import dev.mcd.chess.domain.api.SessionInfo
import dev.mcd.chess.domain.game.GameMessage
import dev.mcd.chess.domain.game.SessionId
import dev.mcd.chess.domain.player.UserId
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.timeout
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import timber.log.Timber
import java.time.Duration
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "api-prefs")

class ChessApiImpl @Inject constructor(
    context: Context,
    private val apiUrl: String,
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

    private val client = HttpClient(CIO) {
        install(WebSockets)
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
                response.userId
            }
        }
    }

    override suspend fun findGame(): SessionInfo {
        return withContext(Dispatchers.IO) {
            client.post {
                url("$apiUrl/game/find")
                timeout {
                    requestTimeoutMillis = Duration.ofMinutes(5).toMillis()
                }
                withBearerToken()

            }.body<SessionInfoSerializer>().domain()
        }
    }

    override suspend fun session(id: SessionId): SessionInfo {
        return withContext(Dispatchers.IO) {
            client.get {
                url("$apiUrl/game/session/$id")
                withBearerToken()
            }.body<SessionInfoSerializer>().domain()
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
                val messageChannel = Channel<GameMessage>(capacity = 1)

                launch {
                    block(
                        object : ActiveGame {
                            override val incoming = messageChannel

                            override suspend fun send(command: String) {
                                println("Sending $command")
                                send(Frame.Text(command))
                            }
                        }
                    )
                }


                for (frame in incoming) {
                    if (frame !is Frame.Text) continue

                    runCatching {
                        val data = DefaultJson.decodeFromString<GameMessageSerializer>(frame.readText())

                        val message = when (data.message) {
                            MessageType.SessionInfo -> data.asSessionInfo()
                            MessageType.ErrorNotUsersMove -> GameMessage.ErrorNotUsersMove
                        }

                        messageChannel.send(message)
                    }.onFailure {
                        Timber.e(it, "Handling frame")
                    }
                }
            }
        }
    }

    override suspend fun storeToken(token: String) {
        store.edit { it[tokenKey] = token }
    }

    override suspend fun userId(): UserId? {
        return store.data.first()[userKey]
    }

    private suspend fun HttpRequestBuilder.withBearerToken() {
        val token = requireNotNull(token()) { "No auth token" }
        bearerAuth(token)
    }

    private suspend fun token(): String? {
        return store.data.first()[tokenKey]
    }

    private suspend fun user(): String? {
        return store.data.first()[userKey]
    }

}
