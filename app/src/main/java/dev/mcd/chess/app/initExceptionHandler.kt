package dev.mcd.chess.app

import android.app.Application
import timber.log.Timber
import java.io.File
import kotlin.system.exitProcess

context(Application)
fun initExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
        Timber.e(throwable)
        // Write throwable stacktrace to log file in data dir
        val crashDir = File(dataDir, "crashes").apply {
            mkdirs()
        }
        File(crashDir, "crash-${System.currentTimeMillis()}.log").apply {
            writeText(throwable.stackTraceToString())
        }
        // Delete old crash logs. Keep the newest 50
        crashDir.listFiles()
            ?.sortedByDescending { it.lastModified() }
            ?.drop(50)
            ?.forEach {
                it.delete()
            }
        exitProcess(0)
    }
}
