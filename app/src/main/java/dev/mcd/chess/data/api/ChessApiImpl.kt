package dev.mcd.chess.data.api

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.mcd.chess.domain.api.ActiveGame
import dev.mcd.chess.domain.api.ChessApi
import dev.mcd.chess.domain.game.GameMessage
import dev.mcd.chess.domain.game.SessionId
import dev.mcd.chess.domain.player.UserId
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.DefaultJson
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import java.time.Duration
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "api-prefs")

class ChessApiImpl @Inject constructor(
    context: Context,
    private val apiHost: String,
) : ChessApi {

    private val store = context.dataStore
    private val tokenKey = stringPreferencesKey("token")
    private val userKey = stringPreferencesKey("user")

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation)

        WebSockets {
            pingInterval = Duration.ofSeconds(15).toMillis()
            maxFrameSize = Long.MAX_VALUE
        }
    }

    override suspend fun generateId(): UserId {
        client.post {
            url {
                host = apiHost
                path("generate_id")
            }
        }.body<AuthResponse>().apply {
            store.edit { it[tokenKey] = token }
            return userId
        }
    }

    override suspend fun findGame(): SessionId {
        return client.post {
            url("$apiHost/game/find")
        }.bodyAsText()
    }

    override suspend fun joinGame(id: SessionId, block: suspend ActiveGame.() -> Unit) {
        val token = requireNotNull(token()) { "No auth token" }

        client.webSocket(
            urlString = "$apiHost/game",
            request = {
                bearerAuth(token)
            }
        ) {
            val messageChannel = Channel<GameMessage>(capacity = 1)

            launch {
                block(
                    object : ActiveGame {
                        override val incoming = messageChannel

                        override suspend fun send(move: String) {
                            send(Frame.Text(move))
                        }
                    }
                )
            }


            for (frame in incoming) {
                if (frame !is Frame.Text) continue

                val data = DefaultJson.decodeFromString<GameMessageSerializer>(frame.readText())

                val message = when (data.message) {
                    MessageType.BoardState -> data.asBoardState()
                    MessageType.ErrorNotUsersMove -> GameMessage.ErrorNotUsersMove
                    MessageType.GameTermination -> GameMessage.GameTermination
                }

                messageChannel.send(message)
            }
        }
    }

    override suspend fun userId(): UserId? {
        return store.data.first()[userKey]
    }

    private suspend fun token(): String? {
        return store.data.first()[tokenKey]
    }

    private suspend fun user(): String? {
        return store.data.first()[userKey]
    }

}