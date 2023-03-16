package dev.mcd.chess.engine.stockfish.data

interface StockfishJni {
    fun main()
    fun readLine(): String
    fun writeLn(cmd: String)
}

class AndroidStockfishJni : StockfishJni {

    init {
        System.loadLibrary("stockfish")
    }

    external override fun main()
    external override fun readLine(): String
    external fun write(command: String): Boolean

    override fun writeLn(cmd: String) {
        write("$cmd\n")
    }
}
