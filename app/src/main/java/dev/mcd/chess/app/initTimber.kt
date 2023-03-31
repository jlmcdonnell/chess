package dev.mcd.chess.app

import android.app.Application
import timber.log.Timber

context(Application)
fun initTimber() {
    Timber.plant(Timber.DebugTree())
}
