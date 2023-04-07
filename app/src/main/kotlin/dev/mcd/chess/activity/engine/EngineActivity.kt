package dev.mcd.chess.activity.engine

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import dev.mcd.chess.engine.lc0.MaiaWeights
import dev.mcd.chess.feature.engine.ActivityEngineProxy
import dev.mcd.chess.feature.engine.AnalyzerEngineProxy
import dev.mcd.chess.feature.engine.BotEngineProxy
import dev.mcd.chess.feature.engine.EngineProxy
import dev.mcd.chess.service.AnalyzerService
import dev.mcd.chess.service.BotService
import dev.mcd.chess.service.EngineService
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

abstract class EngineActivity : ComponentActivity() {

    @Inject
    protected lateinit var analyzerProxy: AnalyzerEngineProxy

    @Inject
    protected lateinit var botProxy: BotEngineProxy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analyzerProxy.bind<Unit, AnalyzerService> {
            AnalyzerService.newIntent(this)
        }

        botProxy.bind<MaiaWeights, BotService> { weight ->
            BotService.newIntent(weight, this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        analyzerProxy.unbind()
        botProxy.unbind()
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified Init, reified T : EngineService<*, *, *>> EngineProxy<*, *>.bind(noinline buildIntent: (Init) -> Intent) {
        (this@bind as ActivityEngineProxy<Init, *, *, *>).bindActivity(this@EngineActivity, buildIntent)
    }

    private fun EngineProxy<*, *>.unbind() {
        runBlocking {
            (this@unbind as ActivityEngineProxy<*, *, *, *>).unbindActivity()
        }
    }
}
