package dev.mcd.chess.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.common.game.online.JoinOnlineGame
import dev.mcd.chess.data.FindGameImpl
import dev.mcd.chess.data.GetGameForUserImpl
import dev.mcd.chess.data.GetOrCreateUserImpl
import dev.mcd.chess.data.game.online.JoinOnlineGameImpl
import dev.mcd.chess.domain.FindGame
import dev.mcd.chess.domain.GetGameForUser
import dev.mcd.chess.domain.GetOrCreateUser

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun joinOnlineGame(impl: JoinOnlineGameImpl): JoinOnlineGame

    @Binds
    abstract fun getOrCreateUser(impl: GetOrCreateUserImpl): GetOrCreateUser

    @Binds
    abstract fun findGame(impl: FindGameImpl): FindGame

    @Binds
    abstract fun getGameForUser(impl: GetGameForUserImpl): GetGameForUser

}
