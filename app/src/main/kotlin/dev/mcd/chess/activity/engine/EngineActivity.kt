package dev.mcd.chess.activity.engine

import android.os.Bundle
import androidx.activity.ComponentActivity
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
        analyzerProxy.bind<AnalyzerService>()
        botProxy.bind<BotService>()
    }

    override fun onDestroy() {
        super.onDestroy()
        analyzerProxy.unbind()
        botProxy.unbind()
    }

    private inline fun <reified T : EngineService<*, *, *>> EngineProxy<*>.bind() {
        val intent = EngineService.newIntent<T>(this@EngineActivity)
        (this@bind as? ActivityEngineProxy<*, *, *>)?.bindActivity(this@EngineActivity, intent)
    }

    private fun EngineProxy<*>.unbind() {
        runBlocking {
            (this@unbind as ActivityEngineProxy<*, *, *>).unbindActivity()
        }
    }
}
