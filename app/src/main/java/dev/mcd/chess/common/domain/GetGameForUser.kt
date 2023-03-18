package dev.mcd.chess.common.domain

import dev.mcd.chess.common.game.GameId

interface GetGameForUser {
    suspend operator fun invoke(): GameId?
}
