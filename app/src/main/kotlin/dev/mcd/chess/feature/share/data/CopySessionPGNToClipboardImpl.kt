package dev.mcd.chess.feature.share.data

import dev.mcd.chess.common.game.GameSession
import dev.mcd.chess.feature.share.domain.CopyPGNToClipboard
import dev.mcd.chess.feature.share.domain.CopySessionPGNToClipboard
import dev.mcd.chess.feature.share.domain.GeneratePGN
import javax.inject.Inject

class CopySessionPGNToClipboardImpl @Inject constructor(
    private val generatePGN: GeneratePGN,
    private val copyPGNToClipboard: CopyPGNToClipboard,
) : CopySessionPGNToClipboard {
    override suspend fun invoke(session: GameSession) {
        val pgn = generatePGN(session)
        copyPGNToClipboard(pgn)
    }
}
