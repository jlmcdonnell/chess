package dev.mcd.chess.feature.engine

import dev.mcd.chess.engine.lc0.FenParam
import dev.mcd.chess.engine.stockfish.data.FenAndDepth

typealias AnalyzerEngineProxy = EngineProxy<FenAndDepth>

typealias BotEngineProxy = EngineProxy<FenParam>
