package dev.mcd.chess.activity.engine

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.activity.engine.adapter.AnalyzerEngineBindingAdapter
import dev.mcd.chess.activity.engine.adapter.BotEngineBindingAdapter
import dev.mcd.chess.activity.engine.adapter.EngineBinderAdapter
import dev.mcd.chess.engine.AnalyzerEngineInterface
import dev.mcd.chess.engine.BotEngineInterface
import dev.mcd.chess.engine.lc0.FenParam
import dev.mcd.chess.engine.stockfish.data.FenAndDepth
import dev.mcd.chess.feature.engine.ActivityEngineProxy
import dev.mcd.chess.feature.engine.AnalyzerEngineProxy
import dev.mcd.chess.feature.engine.BotEngineProxy
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EngineActivityModule {

    @Provides
    @Singleton
    fun analyzerEngineBindingAdapter(): EngineBinderAdapter<FenAndDepth, AnalyzerEngineInterface> {
        return AnalyzerEngineBindingAdapter()
    }

    @Provides
    @Singleton
    fun analyzerProxy(adapter: EngineBinderAdapter<FenAndDepth, AnalyzerEngineInterface>): AnalyzerEngineProxy {
        return ActivityEngineProxy(adapter)
    }

    @Provides
    @Singleton
    fun botEngineBindingAdapter(): EngineBinderAdapter<FenParam, BotEngineInterface> {
        return BotEngineBindingAdapter()
    }

    @Provides
    @Singleton
    fun botProxy(adapter: EngineBinderAdapter<FenParam, BotEngineInterface>): BotEngineProxy {
        return ActivityEngineProxy(adapter)
    }
}
