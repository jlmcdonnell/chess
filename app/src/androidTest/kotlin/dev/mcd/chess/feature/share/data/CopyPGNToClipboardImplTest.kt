package dev.mcd.chess.feature.share.data

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test

class CopyPGNToClipboardImplTest {

    @Test
    fun test() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        CopyPGNToClipboardImpl(context).invoke("PGN text")
    }
}
