package dev.mcd.chess.domain

import dev.mcd.chess.common.game.GameId

interface GetGameForUser {
    suspend operator fun invoke(): GameId?
}
