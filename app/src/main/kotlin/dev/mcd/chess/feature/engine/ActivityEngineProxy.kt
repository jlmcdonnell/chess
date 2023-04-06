package dev.mcd.chess.feature.engine

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.DeadObjectException
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import dev.mcd.chess.engine.EngineInterface
import dev.mcd.chess.feature.engine.ActivityEngineProxy.State.ActivityBound
import dev.mcd.chess.feature.engine.ActivityEngineProxy.State.Ready
import dev.mcd.chess.feature.engine.ActivityEngineProxy.State.UnboundToActivity
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ActivityEngineProxy : EngineProxy {

    private val state = MutableStateFlow<State>(UnboundToActivity)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            runBlocking {
                ifState<ActivityBound> {
                    state.tryEmit(
                        Ready(
                            binder = EngineInterface.Stub.asInterface(service),
                            engineIntent = engineIntent,
                            context = context,
                        ),
                    )
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            runBlocking {
                ifState<Ready> {
                    state.tryEmit(ActivityBound(engineIntent, context))
                }
            }
        }
    }

    fun bindActivity(context: Context, engineIntent: Intent) {
        state.tryEmit(
            ActivityBound(
                context = context,
                engineIntent = engineIntent,
            ),
        )
    }

    suspend fun unbindActivity() {
        ifState<ActivityBound> {
            stop()
            state.emit(UnboundToActivity)
        }
    }

    override suspend fun start() {
        ifState<ActivityBound> {
            startEngine(engineIntent)
        }
    }

    override suspend fun stop() {
        ifState<Ready> {
            context.unbindService(connection)
            context.stopService(engineIntent)
            state.tryEmit(ActivityBound(engineIntent, context))
        }
    }

    override suspend fun getMove(fen: String, depth: Int): String {
        awaitState<Ready>().run {
            try {
                return binder.bestMove(fen, depth)
            } catch (exception: DeadObjectException) {
                throw EngineProxyException.EngineKilledException
            }
        }
    }

    context(ActivityBound)
    private suspend fun startEngine(intent: Intent) {
        if (state.value !is Ready) {
            with(context as ComponentActivity) {
                bindService(intent, connection, BIND_AUTO_CREATE)
                lifecycleScope.launch {
                    try {
                        awaitCancellation()
                    } finally {
                        stopService(intent)
                    }
                }
            }
        }
    }

    private suspend inline fun <reified T : State> awaitState(): T {
        return state.filterIsInstance<T>().first()
    }

    private suspend inline fun <reified T : State> ifState(crossinline block: suspend T.() -> Unit) {
        (state.value as? T)?.apply { block() }
    }

    sealed interface State {
        object UnboundToActivity : State

        open class ActivityBound(
            open val engineIntent: Intent,
            open val context: Context,
        ) : State

        data class Ready(
            override val engineIntent: Intent,
            override val context: Context,
            val binder: EngineInterface,
        ) : ActivityBound(engineIntent, context)
    }
}
