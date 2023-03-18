package dev.mcd.chess.feature.game

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.feature.game.data.BoardSoundsImpl
import dev.mcd.chess.feature.game.data.GameSessionRepositoryImpl
import dev.mcd.chess.feature.game.domain.BoardSounds
import dev.mcd.chess.feature.game.domain.GameSessionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GameModule {
    @Binds
    @Singleton
    abstract fun gameSessionRepo(impl: GameSessionRepositoryImpl): GameSessionRepository

    @Binds
    @Singleton
    abstract fun boardSounds(impl: BoardSoundsImpl): BoardSounds
}
