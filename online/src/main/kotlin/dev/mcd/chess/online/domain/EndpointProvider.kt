package dev.mcd.chess.online.domain

interface EndpointProvider {
    operator fun invoke(): String
}
