package dev.mcd.chess.feature.sound

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.feature.sound.data.BoardSoundPlayerImpl
import dev.mcd.chess.feature.sound.domain.BoardSoundPlayer
import dev.mcd.chess.feature.sound.domain.GameSessionSoundWrapper
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SoundModule {

    @Binds
    @Singleton
    abstract fun soundPlayer(impl: BoardSoundPlayerImpl): BoardSoundPlayer

    @Binds
    @Singleton
    abstract fun sessionWrapper(impl: dev.mcd.chess.feature.sound.data.GameSessionSoundWrapperImpl): GameSessionSoundWrapper

    companion object {
        @Provides
        @GameSessionSounds
        fun gameSessionSoundsScope() = CoroutineScope(Dispatchers.Default + CoroutineName("game-sounds"))
    }
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class GameSessionSounds
