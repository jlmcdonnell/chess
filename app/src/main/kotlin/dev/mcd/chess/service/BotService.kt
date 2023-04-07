package dev.mcd.chess.service

import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import dev.mcd.chess.common.engine.ChessEngine
import dev.mcd.chess.engine.BotEngineInterface
import dev.mcd.chess.engine.lc0.FenParam
import dev.mcd.chess.engine.lc0.Lc0
import dev.mcd.chess.engine.lc0.MaiaWeights
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class BotService : EngineService<MaiaWeights, FenParam, ChessEngine<MaiaWeights, FenParam>>() {

    @Lc0
    @Inject
    internal lateinit var engine: ChessEngine<MaiaWeights, FenParam>

    override fun engine() = engine

    override fun initParams() = MaiaWeights.ELO_1100

    override fun onBind(intent: Intent?): IBinder {
        return object : BotEngineInterface.Stub() {
            override fun bestMove(fen: String): String {
                return runBlocking {
                    engine().getMove(FenParam(fen))
                }
            }
        }
    }
}
