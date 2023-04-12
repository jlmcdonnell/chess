package dev.mcd.chess.feature.engine.binder

import android.os.IBinder
import dev.mcd.chess.engine.BotEngineInterface
import dev.mcd.chess.engine.lc0.FenParam

class BotEngineBindingAdapter : EngineBinderAdapter<FenParam, BotEngineInterface>() {
    override fun castBinder(binder: IBinder): BotEngineInterface {
        return BotEngineInterface.Stub.asInterface(binder)
    }

    override fun move(params: FenParam, binder: BotEngineInterface): String {
        return binder.bestMove(params.fen)
    }
}
