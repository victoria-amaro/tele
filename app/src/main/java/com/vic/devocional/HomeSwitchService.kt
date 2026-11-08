package com.vic.devocional

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent

/** Optional, user-enabled helper. Only watches the stock launcher's main window metadata. */
class HomeSwitchService : AccessibilityService() {
    private val prefs by lazy { getSharedPreferences("amanda", MODE_PRIVATE) }
    private var lastRedirect = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        prefs.edit().putBoolean("browsing_stock", false).apply()
        if (prefs.getBoolean("automatic_home", true)) returnToAmanda()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        if (HomeSwitchPolicy.shouldRedirect(
                prefs.getBoolean("automatic_home", true), prefs.getBoolean("browsing_stock", false),
                event.packageName?.toString(), event.className?.toString()
            ) && SystemClock.elapsedRealtime() - lastRedirect > 1500L) returnToAmanda()
    }

    private fun returnToAmanda() {
        lastRedirect = SystemClock.elapsedRealtime()
        try {
            startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        } catch (_: Exception) {
            android.util.Log.w("AmandaHome", "No se pudo abrir Amanda desde el inicio original")
        }
    }
    override fun onInterrupt() = Unit
}
