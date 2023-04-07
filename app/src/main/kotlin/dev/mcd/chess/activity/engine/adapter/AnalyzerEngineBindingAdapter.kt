package dev.mcd.chess.activity.engine.adapter

import android.os.IBinder
import dev.mcd.chess.engine.AnalyzerEngineInterface
import dev.mcd.chess.engine.stockfish.data.FenAndDepth

class AnalyzerEngineBindingAdapter : EngineBinderAdapter<FenAndDepth, AnalyzerEngineInterface>() {
    override fun castBinder(binder: IBinder): AnalyzerEngineInterface {
        return AnalyzerEngineInterface.Stub.asInterface(binder)
    }

    override fun move(params: FenAndDepth, binder: AnalyzerEngineInterface): String {
        return binder.bestMove(params.fen, params.depth)
    }
}
