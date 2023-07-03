package dev.mcd.chess.feature.common.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.mcd.chess.R
import dev.mcd.chess.feature.common.domain.Translations
import javax.inject.Inject

class TranslationsImpl @Inject constructor(@ApplicationContext private val context: Context) : Translations {

    override val playerYou: String
        get() = context.getString(R.string.player_you)

    override val pgnSiteName: String
        get() = context.getString(R.string.pgn_site_name)

    override val pgnBotName: String
        get() = context.getString(R.string.pgn_bot_game)

    override val pgnOnlineGame: String
        get() = context.getString(R.string.pgn_online_game)

    override val pgnAnalysis: String
        get() = context.getString(R.string.pgn_analysis)

    override fun playerPuzzle(id: String): String = context.getString(R.string.player_puzzle, id)
}
