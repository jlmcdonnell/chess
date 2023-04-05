package dev.mcd.chess.engine.lc0

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.common.engine.ChessEngine
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Named("Lc0")
@Retention(AnnotationRetention.RUNTIME)
annotation class Lc0

@Module
@InstallIn(SingletonComponent::class)
class Lc0Module {
    @Provides
    @Singleton
    internal fun lc0Jni(): Lc0Jni = Lc0JniImpl()

    @Provides
    @Singleton
    @Lc0
    internal fun lc0Engine(
        bridge: Lc0Jni,
        @ApplicationContext context: Context,
    ): ChessEngine {
        val coroutineContext = CoroutineName("Lc0") + Dispatchers.IO
        return Lc0Engine(bridge, context, coroutineContext)
    }
}
