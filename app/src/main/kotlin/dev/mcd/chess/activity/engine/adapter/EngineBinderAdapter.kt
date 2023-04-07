package dev.mcd.chess.activity.engine.adapter

import android.os.IBinder

abstract class EngineBinderAdapter<MoveParams, BinderType> {

    abstract fun castBinder(binder: IBinder): BinderType

    abstract fun move(params: MoveParams, binder: BinderType): String
}
