package dev.mcd.chess.service

import dagger.hilt.android.AndroidEntryPoint
import dev.mcd.chess.common.engine.ChessEngine
import dev.mcd.chess.engine.lc0.Lc0
import javax.inject.Inject

@AndroidEntryPoint
class BotService : EngineService() {

    @Lc0
    @Inject
    internal lateinit var engine: ChessEngine

    override fun engine() = engine
}
