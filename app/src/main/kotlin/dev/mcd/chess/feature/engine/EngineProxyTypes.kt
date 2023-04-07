package dev.mcd.chess.feature.engine

import dev.mcd.chess.engine.lc0.FenParam
import dev.mcd.chess.engine.lc0.MaiaWeights
import dev.mcd.chess.engine.stockfish.data.FenAndDepth

typealias AnalyzerEngineProxy = EngineProxy<Unit, FenAndDepth>

typealias BotEngineProxy = EngineProxy<MaiaWeights, FenParam>
