package dev.mcd.chess.activity.engine

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.feature.engine.ActivityEngineProxy
import dev.mcd.chess.feature.engine.EngineProxy
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EngineActivityModule {

    @AnalyzerEngine
    @Provides
    @Singleton
    fun analyzerProxy(): EngineProxy {
        return ActivityEngineProxy()
    }

    @BotEngine
    @Provides
    @Singleton
    fun botProxy(): EngineProxy {
        return ActivityEngineProxy()
    }
}
