package dev.mcd.chess.feature.engine.binder

import android.os.IBinder

abstract class EngineBinderAdapter<MoveParams, BinderType> {

    abstract fun castBinder(binder: IBinder): BinderType

    abstract fun move(params: MoveParams, binder: BinderType): String
}
