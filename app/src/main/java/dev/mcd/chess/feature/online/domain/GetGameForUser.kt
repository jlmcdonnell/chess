package dev.mcd.chess.feature.online.domain

import dev.mcd.chess.common.game.GameId

interface GetGameForUser {
    suspend operator fun invoke(): GameId?
}
