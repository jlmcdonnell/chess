package dev.mcd.chess.feature.engine

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.DeadObjectException
import android.os.IBinder
import android.os.IInterface
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import dev.mcd.chess.feature.engine.ActivityEngineProxy.State.ActivityBound
import dev.mcd.chess.feature.engine.ActivityEngineProxy.State.Initializing
import dev.mcd.chess.feature.engine.ActivityEngineProxy.State.Ready
import dev.mcd.chess.feature.engine.ActivityEngineProxy.State.UnboundToActivity
import dev.mcd.chess.feature.engine.binder.EngineBinderAdapter
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ActivityEngineProxy<Init, Move, Binder : IInterface, Adapter : EngineBinderAdapter<Move, Binder>>(
    private val adapter: Adapter,
) : EngineProxy<Init, Move> {

    private val state = MutableStateFlow<State>(UnboundToActivity)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            runBlocking {
                ifState<Initializing<Init>> {
                    state.tryEmit(
                        Ready(
                            binder = adapter.castBinder(service),
                            engineIntent = buildIntent(initParams),
                            context = context,
                            buildIntent = buildIntent,
                        ),
                    )
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            runBlocking {
                ifState<Ready<Init, Binder>> {
                    state.tryEmit(ActivityBound(context, buildIntent))
                }
            }
        }
    }

    fun bindActivity(context: Context, buildIntent: (Init) -> Intent) {
        state.tryEmit(
            ActivityBound(
                context = context,
                buildIntent = buildIntent,
            ),
        )
    }

    suspend fun unbindActivity() {
        ifState<ActivityBound<Init>> {
            stop()
            state.emit(UnboundToActivity)
        }
    }

    override suspend fun start(initParams: Init) {
        ifState<ActivityBound<Init>> {
            startEngine(initParams, buildIntent(initParams))
        }
    }

    override suspend fun stop() {
        ifState<Ready<Init, Binder>> {
            context.unbindService(connection)
            context.stopService(engineIntent)
            state.tryEmit(ActivityBound(context, buildIntent))
        }
    }

    override suspend fun getMove(params: Move): String {
        awaitState<Ready<Init, Binder>>().run {
            try {
                return adapter.move(params, binder)
            } catch (exception: DeadObjectException) {
                throw EngineProxyException.EngineKilledException
            }
        }
    }

    context(ActivityBound<Init>)
    private suspend fun startEngine(params: Init, intent: Intent) {
        if (state.value !is Ready<*, *>) {
            with(context as ComponentActivity) {
                bindService(intent, connection, BIND_AUTO_CREATE)
                state.tryEmit(
                    Initializing(
                        context = context,
                        buildIntent = buildIntent,
                        initParams = params,
                    ),
                )
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

    private sealed interface State {
        object UnboundToActivity : State

        open class ActivityBound<Init>(
            open val context: Context,
            open val buildIntent: (Init) -> Intent,
        ) : State

        open class Initializing<Init>(
            context: Context,
            buildIntent: (Init) -> Intent,
            val initParams: Init,
        ) : ActivityBound<Init>(context, buildIntent)

        data class Ready<Init, Binder>(
            override val context: Context,
            override val buildIntent: (Init) -> Intent,
            val engineIntent: Intent,
            val binder: Binder,
        ) : ActivityBound<Init>(context, buildIntent)
    }
}
