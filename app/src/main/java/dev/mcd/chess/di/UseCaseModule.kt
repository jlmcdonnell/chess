package dev.mcd.chess.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.game.domain.JoinOnlineGame
import dev.mcd.chess.common.data.FindGameImpl
import dev.mcd.chess.common.data.GetGameForUserImpl
import dev.mcd.chess.common.data.GetLobbyInfoImpl
import dev.mcd.chess.common.data.GetOrCreateUserImpl
import dev.mcd.chess.common.data.JoinOnlineGameImpl
import dev.mcd.chess.common.domain.FindGame
import dev.mcd.chess.common.domain.GetGameForUser
import dev.mcd.chess.common.domain.GetLobbyInfo
import dev.mcd.chess.common.domain.GetOrCreateUser

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

    @Binds
    abstract fun getLobbyInfo(impl: GetLobbyInfoImpl): GetLobbyInfo

}
