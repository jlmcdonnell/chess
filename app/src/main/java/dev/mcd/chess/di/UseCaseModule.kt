package dev.mcd.chess.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.data.game.online.JoinOnlineGameImpl
import dev.mcd.chess.domain.game.online.JoinOnlineGame

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun joinOnlineGame(impl: JoinOnlineGameImpl): JoinOnlineGame

}
