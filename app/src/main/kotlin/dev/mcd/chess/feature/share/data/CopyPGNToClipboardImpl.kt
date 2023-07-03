package dev.mcd.chess.feature.share.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.mcd.chess.feature.share.domain.CopyPGNToClipboard
import timber.log.Timber
import javax.inject.Inject

class CopyPGNToClipboardImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : CopyPGNToClipboard {
    override fun invoke(pgn: String) {
        val clipboard = context.getSystemService<ClipboardManager>()!!
        val clipData = ClipData(
            "Chess PGN",
            arrayOf(
                "text/plain",
                "application/vnd.chess-pgn",
                "application/x-chess-pgn",
            ),
            ClipData.Item(pgn),
        )
        clipboard.setPrimaryClip(clipData)
        Timber.d(pgn)
    }
}
