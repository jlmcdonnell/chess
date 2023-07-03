package dev.mcd.chess.feature.share.domain

interface CopyPGNToClipboard {
    operator fun invoke(pgn: String)
}
