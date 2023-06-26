package dev.mcd.chess.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By.desc
import androidx.test.uiautomator.By.text
import androidx.test.uiautomator.Until.hasObject
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        rule.collectBaselineProfile("dev.mcd.chess") {
            pressHome()
            startActivityAndWait()
            device.findObject(text("Play Computer")).click()
            device.findObject(desc("BLACK")).click()
            device.findObject(text("Pawn Pioneer")).click()
            device.wait(hasObject(text("never")), 2500)
            device.findObject(desc("Undo move")).click()
            device.wait(hasObject(text("never")), 500)
            device.findObject(desc("Redo move")).click()
            device.wait(hasObject(text("never")), 500)
        }
    }
}
