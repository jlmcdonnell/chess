package dev.mcd.chess.service

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
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

    private var weight: MaiaWeights? = null

    override fun engine() = engine

    override fun initParams() = weight ?: throw IllegalStateException("Weight not set (should be set via onBind)")

    override fun onBind(intent: Intent?): IBinder {
        if (weight != null) {
            throw IllegalStateException("Already bound")
        }

        weight = intent?.getStringExtra(EXTRA_WEIGHT)?.let {
            MaiaWeights.valueOf(it)
        } ?: throw IllegalArgumentException("Missing weight")

        return object : BotEngineInterface.Stub() {
            override fun bestMove(fen: String): String {
                return runBlocking {
                    engine().getMove(FenParam(fen))
                }
            }
        }
    }

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
        weight = null
    }

    companion object {
        const val EXTRA_WEIGHT = "extra_weight"

        fun newIntent(weight: MaiaWeights, context: Context) = Intent(context, BotService::class.java).apply {
            putExtra(EXTRA_WEIGHT, weight.name)
        }
    }
}
