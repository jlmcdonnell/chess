package dev.mcd.chess.feature.share

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.mcd.chess.feature.common.domain.Translations
import dev.mcd.chess.feature.share.data.CopyPGNToClipboardImpl
import dev.mcd.chess.feature.share.data.CopySessionPGNToClipboardImpl
import dev.mcd.chess.feature.share.data.GeneratePGNImpl
import dev.mcd.chess.feature.share.domain.CopyPGNToClipboard
import dev.mcd.chess.feature.share.domain.CopySessionPGNToClipboard
import dev.mcd.chess.feature.share.domain.GeneratePGN
import java.time.LocalDate
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ShareModule {

    @Binds
    abstract fun copyPGNToClipboard(impl: CopyPGNToClipboardImpl): CopyPGNToClipboard

    @Binds
    abstract fun copySessionPGNToClipboard(impl: CopySessionPGNToClipboardImpl): CopySessionPGNToClipboard

    companion object {
        @Provides
        @Singleton
        fun generatePGN(translations: Translations): GeneratePGN = GeneratePGNImpl(
            translations = translations,
            provideDate = {
                LocalDate.now()
            },
        )
    }
}
