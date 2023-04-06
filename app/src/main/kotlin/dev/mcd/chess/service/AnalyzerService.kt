package dev.mcd.chess.service

import dagger.hilt.android.AndroidEntryPoint
import dev.mcd.chess.common.engine.ChessEngine
import dev.mcd.chess.engine.stockfish.Stockfish
import javax.inject.Inject

@AndroidEntryPoint
class AnalyzerService : EngineService() {

    @Stockfish
    @Inject
    internal lateinit var engine: ChessEngine

    override fun engine() = engine
}
