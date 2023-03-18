package dev.mcd.chess.online.data

import dev.mcd.chess.online.domain.ChessApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore("To be run ad-hoc for E2E testing")
class ChessApiImplTest {

    private lateinit var api: ChessApi

    @Before
    fun setUp() {
        val client = HttpClient(CIO) {
            install(WebSockets) {
                pingInterval = 1500
            }
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                this.level = LogLevel.BODY
            }
        }
        api = ChessApiImpl("http://127.0.0.1:8080", client)
    }

    @Test
    fun `Run API flow`() = runBlocking {
        // ...
    }
}
