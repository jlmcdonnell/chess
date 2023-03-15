package dev.mcd.chess.engine.data

import timber.log.Timber

class StockfishJni {
    external fun init()
    external fun main()
    private external fun write(command: String): Boolean
    external fun readLine(): String

    fun writeLn(cmd: String) {
        Timber.d("writeLn: $cmd")
        write("$cmd\n")
    }

    companion object {
        init {
            System.loadLibrary("stockfish")
        }
    }
}
