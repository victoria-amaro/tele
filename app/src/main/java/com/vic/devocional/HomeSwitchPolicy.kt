package com.vic.devocional

object HomeSwitchPolicy {
    const val STOCK_PACKAGE = "com.google.android.tvlauncher"
    const val STOCK_ACTIVITY = "com.google.android.tvlauncher.MainActivity"
    fun shouldRedirect(automatic: Boolean, browsingStock: Boolean, packageName: String?, className: String?): Boolean =
        automatic && !browsingStock && packageName == STOCK_PACKAGE && className == STOCK_ACTIVITY
}
