package dev.mcd.chess.feature.online

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.feature.online.data.FindGameImpl
import dev.mcd.chess.feature.online.data.GetGameForUserImpl
import dev.mcd.chess.feature.online.data.GetLobbyInfoImpl
import dev.mcd.chess.feature.online.data.GetOrCreateUserImpl
import dev.mcd.chess.feature.online.data.JoinOnlineGameImpl
import dev.mcd.chess.feature.online.domain.FindGame
import dev.mcd.chess.feature.online.domain.GetGameForUser
import dev.mcd.chess.feature.online.domain.GetLobbyInfo
import dev.mcd.chess.feature.online.domain.GetOrCreateUser
import dev.mcd.chess.feature.online.domain.JoinOnlineGame

@Module
@InstallIn(SingletonComponent::class)
abstract class OnlineModule {
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
