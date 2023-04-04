package dev.mcd.chess.feature.game

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.feature.game.data.GameSessionRepositoryImpl
import dev.mcd.chess.feature.game.data.usecase.StartBotGameImpl
import dev.mcd.chess.feature.game.domain.GameSessionRepository
import dev.mcd.chess.feature.game.domain.usecase.MoveForBot
import dev.mcd.chess.feature.game.domain.usecase.MoveForBotImpl
import dev.mcd.chess.feature.game.domain.usecase.StartBotGame
import dev.mcd.chess.feature.puzzle.data.usecase.CreatePuzzleSessionImpl
import dev.mcd.chess.feature.puzzle.domain.usecase.CreatePuzzleSession
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GameModule {
    @Binds
    @Singleton
    abstract fun gameSessionRepo(impl: GameSessionRepositoryImpl): GameSessionRepository

    @Binds
    abstract fun startBotGame(impl: StartBotGameImpl): StartBotGame

    @Binds
    abstract fun moveForBot(impl: MoveForBotImpl): MoveForBot

    @Binds
    abstract fun createPuzzleSession(impl: CreatePuzzleSessionImpl): CreatePuzzleSession
}
