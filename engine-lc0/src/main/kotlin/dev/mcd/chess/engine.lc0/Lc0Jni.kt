package dev.mcd.chess.engine.lc0

internal interface Lc0Jni {
    fun init()
    fun main(weightsPath: String)
    fun readLine(): String?
    fun readError(): String?
    fun writeLine(cmd: String)
}

internal class Lc0JniImpl : Lc0Jni {

    override fun init() {
        System.loadLibrary("bridge")
    }

    external override fun main(weightsPath: String)
    external override fun readLine(): String
    external override fun readError(): String
    private external fun write(command: String): Boolean

    override fun writeLine(cmd: String) {
        write("$cmd\n")
    }
}
