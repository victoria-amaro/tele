package com.vic.devocional

import org.junit.Assert.*
import org.junit.Test

class HomeSwitchPolicyTest {
    @Test fun returnsToAmandaWhenStockHomeAppearsInAutomaticMode() {
        assertTrue(HomeSwitchPolicy.shouldRedirect(true, false, HomeSwitchPolicy.STOCK_PACKAGE, HomeSwitchPolicy.STOCK_ACTIVITY))
    }
    @Test fun leavesGoogleTvVisibleWhenUserOpenedIt() {
        assertFalse(HomeSwitchPolicy.shouldRedirect(true, true, HomeSwitchPolicy.STOCK_PACKAGE, HomeSwitchPolicy.STOCK_ACTIVITY))
    }
    @Test fun respectsGoogleTvAsChosenDefault() {
        assertFalse(HomeSwitchPolicy.shouldRedirect(false, false, HomeSwitchPolicy.STOCK_PACKAGE, HomeSwitchPolicy.STOCK_ACTIVITY))
    }
    @Test fun doesNotInterruptSettingsOrOtherGoogleTvScreens() {
        assertFalse(HomeSwitchPolicy.shouldRedirect(true, false, HomeSwitchPolicy.STOCK_PACKAGE, "com.google.android.tvlauncher.SettingsActivity"))
    }
    @Test fun doesNotInterruptStreamingAppsOrScreensaver() {
        assertFalse(HomeSwitchPolicy.shouldRedirect(true, false, "com.google.android.backdrop", HomeSwitchPolicy.STOCK_ACTIVITY))
    }
    @Test fun ignoresIncompleteEvents() {
        assertFalse(HomeSwitchPolicy.shouldRedirect(true, false, null, null))
    }
}
