package com.vic.devocional

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.accessibilityservice.AccessibilityServiceInfo
import android.widget.Toast

class HomeNavigation(private val activity: Activity) {
    private val prefs = activity.getSharedPreferences("amanda", Activity.MODE_PRIVATE)

    fun openSettings() = open(Intent(Settings.ACTION_SETTINGS), "Abrí Ajustes con el botón de la tele")

    fun chooseMode() {
        AlertDialog.Builder(activity).setTitle("Tipo de inicio")
            .setItems(arrayOf("Amanda al encender", "Ver inicio Google TV ahora", "Google TV como inicio habitual", "Configurar regreso automático a Amanda")) { _, choice ->
                when (choice) {
                    0 -> {
                        prefs.edit().putBoolean("automatic_home", true).putBoolean("browsing_stock", false).apply()
                        if (stockEnabled() && !helperEnabled()) explainHelper()
                        else toast("Amanda seleccionada. Probá Inicio y el próximo encendido.")
                    }
                    1 -> openStock()
                    2 -> openStock(asDefault = true)
                    3 -> explainHelper()
                }
            }.setNegativeButton("Volver", null).show()
    }

    fun openStock(asDefault: Boolean = false) {
        if (!stockEnabled()) {
            AlertDialog.Builder(activity).setTitle("Habilitar el inicio Google TV")
                .setMessage("Android TV Home está desactivado. Para usar este botón debe habilitarse. Si querés mantener Amanda al encender, configurá también su regreso automático. En la pantalla siguiente podés habilitar Android TV Home.")
                .setPositiveButton("Ver aplicación") { _, _ ->
                    open(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${HomeSwitchPolicy.STOCK_PACKAGE}")), "Abrí Aplicaciones → Android TV Home → Habilitar")
                }.setNegativeButton("Volver", null).show()
            return
        }
        val previousAutomatic = prefs.getBoolean("automatic_home", true)
        prefs.edit().putBoolean("browsing_stock", true).apply()
        if (asDefault) prefs.edit().putBoolean("automatic_home", false).apply()
        try {
            activity.startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                .setClassName(HomeSwitchPolicy.STOCK_PACKAGE, HomeSwitchPolicy.STOCK_ACTIVITY)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: Exception) {
            prefs.edit().putBoolean("browsing_stock", false).putBoolean("automatic_home", previousAutomatic).apply()
            toast("No se pudo abrir Google TV. Revisá que Android TV Home esté habilitado.")
        }
    }

    private fun stockEnabled(): Boolean = try {
        activity.packageManager.getApplicationInfo(HomeSwitchPolicy.STOCK_PACKAGE, PackageManager.MATCH_DISABLED_COMPONENTS).enabled
    } catch (_: Exception) { false }

    private fun helperEnabled(): Boolean {
        val manager = activity.getSystemService(AccessibilityManager::class.java)
        return manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK).any {
            it.resolveInfo.serviceInfo.packageName == activity.packageName && it.resolveInfo.serviceInfo.name == HomeSwitchService::class.java.name
        }
    }

    private fun explainHelper() {
        AlertDialog.Builder(activity).setTitle("Regreso automático a Amanda")
            .setMessage("Esta opción usa un servicio de accesibilidad opcional. Detecta únicamente cuándo aparece la ventana principal de Android TV Home y abre Amanda. No lee textos, fotos ni contraseñas, no pulsa botones y no envía datos por internet. Cuando elegís «Inicio Google TV», lo deja abierto hasta que vuelvas a Amanda. Android te mostrará su aviso general de accesibilidad. Podés desactivarlo en cualquier momento.")
            .setPositiveButton("Abrir accesibilidad") { _, _ ->
                open(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), "Abrí Ajustes → Accesibilidad → Regreso a Amanda")
            }.setNegativeButton("Ahora no", null).show()
    }

    private fun open(intent: Intent, fallback: String) {
        try { activity.startActivity(intent) } catch (_: Exception) { toast(fallback) }
    }
    private fun toast(message: String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
}
