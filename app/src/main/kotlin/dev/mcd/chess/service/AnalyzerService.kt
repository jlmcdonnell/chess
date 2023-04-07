package dev.mcd.chess.service

import android.content.Context
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import dev.mcd.chess.common.engine.ChessEngine
import dev.mcd.chess.engine.AnalyzerEngineInterface
import dev.mcd.chess.engine.stockfish.Stockfish
import dev.mcd.chess.engine.stockfish.data.FenAndDepth
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class AnalyzerService : EngineService<Unit, FenAndDepth, ChessEngine<Unit, FenAndDepth>>() {

    @Stockfish
    @Inject
    internal lateinit var engine: ChessEngine<Unit, FenAndDepth>

    override fun initParams() = Unit

    override fun engine() = engine

    override fun onBind(intent: Intent?): IBinder {
        return object : AnalyzerEngineInterface.Stub() {
            override fun bestMove(fen: String, depth: Int): String {
                return runBlocking {
                    engine().getMove(FenAndDepth(fen, depth))
                }
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, AnalyzerService::class.java)
        }
    }
}
