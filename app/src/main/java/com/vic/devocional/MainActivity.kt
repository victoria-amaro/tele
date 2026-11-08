package com.vic.devocional

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.json.JSONArray
import java.time.Instant
import java.io.File
import java.util.concurrent.Executors

/** Native TV UI. Positions use a 1920 x 1080 design surface, scaled to the display. */
class MainActivity : Activity() {
    private val ink = Color.rgb(49, 59, 22)
    private val olive = Color.rgb(77, 89, 30)
    private val cream = Color.rgb(255, 249, 238)
    private val cardColor = Color.rgb(244, 234, 216)
    private val gold = Color.rgb(190, 146, 43)
    private val handler = Handler(Looper.getMainLooper())
    private val worker = Executors.newSingleThreadExecutor()
    private val prefs by lazy { getSharedPreferences("amanda", MODE_PRIVATE) }
    private lateinit var clock: TextView
    private lateinit var date: TextView
    private lateinit var verse: TextView
    private lateinit var reference: TextView
    private lateinit var reflection: TextView
    private lateinit var photo: ImageView
    private lateinit var photoHint: TextView
    private lateinit var photoFrame: FrameLayout
    private lateinit var items: JSONArray
    private lateinit var photos: PhotoStore
    private var photoIndex = 0
    private var photoGeneration = 0
    private var photoBusy = false
    private var active = false
    private var lastPhotoAt = 0L
    private var fullPhoto: ImageView? = null
    private var photoDialog: AlertDialog? = null
    private var pendingImport = 0
    private var appSnapshotKey = ""
    private val ticker = object : Runnable {
        override fun run() {
            if (!active) return
            showDay()
            if (System.currentTimeMillis() - lastPhotoAt >= 15000) nextPhoto()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        items = JSONArray(assets.open("devocional.json").bufferedReader().use { it.readText() })
        photos = PhotoStore(this)
        val apps = installed()
        appSnapshotKey = appsKey(apps)
        setContentView(buildUi(apps))
    }

    private fun text(value: String, size: Float, serif: Boolean = false, bold: Boolean = false) = TextView(this).apply {
        text = value; setTextColor(ink)
        setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, size)
        typeface = Typeface.create(if (serif) "serif" else "sans-serif", if (bold) Typeface.BOLD else Typeface.NORMAL)
        includeFontPadding = false
        importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
    }

    private fun background(color: Int, stroke: Int = 0) = GradientDrawable().apply {
        setColor(color); cornerRadius = 28f
        if (stroke != 0) setStroke(6, stroke)
    }

    private fun place(parent: FrameLayout, view: View, x: Int, y: Int, w: Int, h: Int) {
        parent.addView(view, FrameLayout.LayoutParams(w, h).apply { leftMargin = x; topMargin = y })
    }

    private fun focusable(view: View, onClick: () -> Unit) {
        view.id = View.generateViewId()
        view.isFocusable = true; view.isClickable = true
        view.background = background(cardColor)
        view.setOnFocusChangeListener { _, focused -> view.background = background(cardColor, if (focused) gold else 0) }
        view.setOnClickListener { onClick() }
    }

    private fun buildUi(availableApps: List<ResolveInfo>): View {
        val design = FrameLayout(this).apply { setBackgroundColor(cream) }
        clock = text("", 140f, bold = true)
        date = text("", 42f)
        place(design, clock, 64, 38, 700, 160)
        place(design, date, 68, 190, 900, 60)
        place(design, ImageView(this).apply { setImageResource(R.drawable.flowers); importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO }, 1730, 24, 140, 140)
        place(design, text("Bienvenida, Amanda", 58f, serif = true), 1000, 90, 730, 90)
        place(design, View(this).apply { setBackgroundColor(gold) }, 1210, 186, 500, 3)
        val settings = text("Inicio y ajustes", 30f, bold = true).apply { gravity = Gravity.CENTER }
        focusable(settings) { settingsMenu() }
        place(design, settings, 1510, 208, 346, 54)

        val devotional = FrameLayout(this)
        focusable(devotional) { showDevotional() }
        devotional.contentDescription = "Devocional de hoy. OK para leer completo"
        place(design, devotional, 64, 280, 660, 520)
        place(devotional, text("Devocional de hoy", 42f, bold = true), 32, 26, 596, 56)
        verse = text("", 47f, serif = true, bold = true).apply { maxLines = 3; ellipsize = android.text.TextUtils.TruncateAt.END }
        place(devotional, verse, 32, 98, 596, 180)
        reference = text("", 29f)
        place(devotional, reference, 32, 286, 596, 42)
        place(devotional, View(this).apply { setBackgroundColor(gold) }, 32, 340, 596, 2)
        place(devotional, text("Para reflexionar", 28f, bold = true), 32, 355, 596, 38)
        reflection = text("", 31f).apply { maxLines = 3; ellipsize = android.text.TextUtils.TruncateAt.END }
        place(devotional, reflection, 32, 398, 596, 116)

        photoFrame = FrameLayout(this)
        focusable(photoFrame) { photoMenu() }
        photoFrame.contentDescription = "Fotos de la familia. OK para ver opciones"
        photoFrame.clipToOutline = true
        place(design, photoFrame, 748, 280, 1108, 520)
        photo = ImageView(this).apply { scaleType = ImageView.ScaleType.CENTER_CROP; contentDescription = "Foto familiar" }
        place(photoFrame, photo, 8, 8, 1092, 504)
        photoHint = text("Nuestra familia\nOK para agregar fotos", 44f, bold = true).apply {
            gravity = Gravity.CENTER; setPadding(36, 16, 36, 16)
            setBackgroundColor(Color.argb(160, 255, 249, 238))
        }
        place(photoFrame, photoHint, 20, 350, 1068, 145)

        val labels = listOf("TV", "YouTube", "Disney+", "Netflix", "Prime Video", "HBO Max")
        val tiles = labels.mapIndexed { index, label ->
            val tile = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(18, 14, 18, 14) }
            val target = findTarget(label, availableApps)
            if (label == "TV") {
                tile.addView(text("▣", 76f).apply { gravity = Gravity.CENTER }, LinearLayout.LayoutParams(-1, 92))
            } else if (target != null) {
                tile.addView(ImageView(this).apply {
                    // The icon comes from the installed app, never a recreated brand logo.
                    setImageDrawable(target.loadIcon(packageManager)); scaleType = ImageView.ScaleType.FIT_CENTER
                    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                }, LinearLayout.LayoutParams(-1, 92))
            } else {
                tile.addView(text("—", 60f).apply { gravity = Gravity.CENTER }, LinearLayout.LayoutParams(-1, 92))
            }
            val caption = text(label, 31f, bold = true).apply { gravity = Gravity.CENTER; maxLines = 1 }
            tile.addView(caption, LinearLayout.LayoutParams(-1, 48))
            tile.contentDescription = "$label. ${if (target == null) "Elegir aplicación" else "Abrir"}"
            focusable(tile) { launch(label) }
            tile.setOnFocusChangeListener { _, focused ->
                tile.background = background(if (focused) olive else cardColor, if (focused) gold else 0)
                caption.setTextColor(if (focused) Color.WHITE else ink)
            }
            tile.setOnLongClickListener { chooseApp(label); true }
            place(design, tile, 64 + index * 302, 828, 282, 176)
            tile.nextFocusUpId = if (index < 2) devotional.id else photoFrame.id
            tile
        }
        tiles.forEachIndexed { index, tile ->
            tile.nextFocusLeftId = tiles[(index - 1).coerceAtLeast(0)].id
            tile.nextFocusRightId = tiles[(index + 1).coerceAtMost(5)].id
            tile.nextFocusDownId = tile.id
        }
        devotional.nextFocusDownId = tiles[0].id
        devotional.nextFocusRightId = photoFrame.id
        photoFrame.nextFocusLeftId = devotional.id
        photoFrame.nextFocusDownId = tiles[2].id
        photoFrame.nextFocusUpId = settings.id
        settings.nextFocusDownId = photoFrame.id
        place(design, text("Flechas para elegir · OK para abrir · Mantené OK para cambiar una app", 27f).apply { gravity = Gravity.CENTER }, 64, 1020, 1792, 42)
        tiles[0].requestFocus()
        return DesignSurface(this, design)
    }

    private fun installed(): List<ResolveInfo> = packageManager.queryIntentActivities(
        Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER), 0
    ).filter { it.activityInfo.packageName != packageName }
        .sortedBy { it.loadLabel(packageManager).toString().lowercase() }

    private fun settingsMenu() {
        AlertDialog.Builder(this).setTitle("Inicio y ajustes")
            .setItems(arrayOf("Elegir pantalla de inicio", "Aplicaciones instaladas y almacenamiento", "Elegir reproductor de YouTube", "Google Fotos: protector de la tele")) { _, index ->
                when (index) {
                    0 -> chooseHome()
                    1 -> try { startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_SETTINGS)) }
                        catch (_: Exception) { toast("Abrí Ajustes de la tele → Aplicaciones") }
                    2 -> chooseApp("YouTube")
                    3 -> openAmbientSettings()
                }
            }.setNegativeButton("Volver", null).show()
    }

    private fun chooseHome() {
        AlertDialog.Builder(this).setTitle("Usar Amanda como inicio")
            .setMessage("En la pantalla siguiente, elegí Amanda como aplicación de inicio. Algunos televisores no ofrecen este selector. Si no aparece, no desactives Android TV Home: hace falta revisar el método compatible con tu tele. Para volver atrás, elegí Android TV Home o Google TV en ese mismo selector.")
            .setPositiveButton("Elegir inicio") { _, _ ->
                try { startActivity(Intent(android.provider.Settings.ACTION_HOME_SETTINGS)) }
                catch (_: Exception) {
                    AlertDialog.Builder(this).setTitle("Esta tele no ofrece el selector de inicio")
                        .setMessage("Amanda está preparada como launcher, pero el fabricante no permite elegirlo desde esta pantalla. Volvé a la guía del proyecto para continuar con el diagnóstico. No se cambió ni se desactivó el inicio actual.")
                        .setPositiveButton("Entendido", null).show()
                }
            }.setNegativeButton("Volver", null).show()
    }

    private fun appsKey(apps: List<ResolveInfo>) = apps.joinToString("|") { "${it.activityInfo.packageName}/${it.activityInfo.name}" }

    private fun findTarget(label: String, all: List<ResolveInfo> = installed()): ResolveInfo? {
        val saved = prefs.getString(label, null)
        if (label == "YouTube") {
            val preferred = YouTubeChoice.preferred(all.map { it.activityInfo.packageName }.toSet(), saved?.substringBefore('/'))
            return all.firstOrNull { it.activityInfo.packageName == preferred }
        }
        if (saved != null) return all.firstOrNull { "${it.activityInfo.packageName}/${it.activityInfo.name}" == saved }
        val packages = when (label) {
            "Disney+" -> listOf("com.disney.disneyplus")
            "Netflix" -> listOf("com.netflix.ninja", "com.netflix.mediaclient")
            "Prime Video" -> listOf("com.amazon.amazonvideo.livingroom", "com.amazon.avod.thirdpartyclient")
            "HBO Max" -> listOf("com.wbd.stream", "com.hbo.hbonow")
            else -> emptyList()
        }
        packages.forEach { pkg -> all.firstOrNull { it.activityInfo.packageName == pkg }?.let { return it } }
        return if (label == "TV") all.firstOrNull {
            it.loadLabel(packageManager).toString().lowercase().let { name ->
                name == "tv" || name.contains("live tv") || name.contains("tv en vivo") || name.contains("tv en directo")
            }
        } else null
    }

    private fun chooseApp(label: String) {
        val apps = installed()
        if (apps.isEmpty()) { toast("No encontré aplicaciones de TV"); return }
        AlertDialog.Builder(this).setTitle("Elegí qué abre $label")
            .setItems(apps.map { "${it.loadLabel(packageManager)} (${it.activityInfo.packageName})" }.toTypedArray()) { _, index ->
                val app = apps[index]
                prefs.edit().putString(label, "${app.activityInfo.packageName}/${app.activityInfo.name}").apply()
                recreate()
            }.setNegativeButton("Cancelar", null).show()
    }

    private fun launch(label: String) {
        val target = findTarget(label)
        if (target == null) { toast("Elegí la app para $label. Si falta, instalala primero."); chooseApp(label); return }
        val open = {
            try {
                startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
                    .setClassName(target.activityInfo.packageName, target.activityInfo.name))
            } catch (_: Exception) { toast("No se pudo abrir $label. Mantené OK para elegir otra app.") }
        }
        if (label == "YouTube" && target.activityInfo.packageName == YouTubeChoice.OFFICIAL) {
            AlertDialog.Builder(this).setTitle("YouTube oficial")
                .setMessage("Se abrirá YouTube oficial, que puede mostrar anuncios. Amanda no bloquea esa publicidad. Para usar SmartTube hay que instalarlo por separado desde su proyecto oficial y volver a abrir Amanda.")
                .setPositiveButton("Abrir oficial") { _, _ -> open() }
                .setNegativeButton("Cancelar", null).show()
        } else open()
    }

    private fun showDay() {
        val now = Instant.now()
        clock.text = DailyContent.time(now); date.text = DailyContent.date(now)
        val item = items.getJSONObject(DailyContent.index(now, items.length()))
        verse.text = "«${item.getString("texto")}»"
        reference.text = "${item.getString("ref")} · RV 1909"
        reflection.text = item.getString("reflexion")
    }

    private fun showDevotional() {
        val item = items.getJSONObject(DailyContent.index(Instant.now(), items.length()))
        AlertDialog.Builder(this).setTitle("Devocional de hoy")
            .setMessage("${item.getString("texto")}\n${item.getString("ref")} · Reina-Valera 1909\n\n${item.getString("reflexion")}")
            .setPositiveButton("Volver", null).show()
    }

    private fun photoMenu() {
        AlertDialog.Builder(this).setTitle("Nuestra familia")
            .setItems(arrayOf("Ver fotos en pantalla completa", "Agregar fotos a esta app", "Google Fotos: ajustar protector de la tele", "Borrar fotos importadas de esta app")) { _, index ->
                when (index) {
                    0 -> fullscreenPhotos()
                    1 -> importPhotos()
                    2 -> openAmbientSettings()
                    3 -> AlertDialog.Builder(this).setTitle("¿Borrar las copias importadas?")
                        .setMessage("No borra fotos de Google Fotos ni del dispositivo de origen.")
                        .setPositiveButton("Borrar") { _, _ ->
                            photoGeneration++; photo.setImageDrawable(null); fullPhoto?.setImageDrawable(null)
                            worker.execute { photos.clear(); handler.post { if (!isDestroyed) { photoIndex = 0; nextPhoto() } } }
                        }.setNegativeButton("Cancelar", null).show()
                }
            }.setNegativeButton("Volver", null).show()
    }

    private fun openAmbientSettings() {
        try {
            startActivity(Intent(android.provider.Settings.ACTION_DREAM_SETTINGS))
        } catch (_: Exception) {
            AlertDialog.Builder(this).setTitle("Protector de la tele")
                .setMessage("Buscá en Ajustes de la tele → Sistema → Modo ambiente o Protector de pantalla → Google Fotos. El nombre y la disponibilidad dependen de tu Panavox. Esta app no cambia esos ajustes ni accede a las fotos de tu cuenta.")
                .setPositiveButton("Entendido", null).show()
        }
    }
    private fun fullscreenPhotos() {
        if (photos.sources().isEmpty()) { importPhotos(); return }
        val image = ImageView(this).apply {
            setBackgroundColor(Color.BLACK); scaleType = ImageView.ScaleType.FIT_CENTER
            contentDescription = "Fotos familiares. Atrás para volver"
        }
        fullPhoto = image
        photoDialog = AlertDialog.Builder(this).setView(image).setPositiveButton("Volver", null).create().apply {
            setOnDismissListener { fullPhoto = null; photoDialog = null }
            show(); window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        photoIndex--; nextPhoto()
    }

    private fun importPhotos() {
        try {
            startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE); type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }, 100)
        } catch (_: android.content.ActivityNotFoundException) {
            AlertDialog.Builder(this).setTitle("Esta tele no tiene selector de fotos")
                .setMessage("Podés agregar fotos a app/src/main/assets/photos desde GitHub y recompilar. La conexión directa a Google Fotos todavía no está configurada.")
                .setPositiveButton("Entendido", null).show()
        }
    }

    @Deprecated("Activity result API without AndroidX dependencies")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != 100 || resultCode != RESULT_OK || data == null) return
        val uris = data.clipData?.let { clip -> (0 until clip.itemCount).map { clip.getItemAt(it).uri } }
            ?: listOfNotNull(data.data)
        pendingImport++
        val generation = pendingImport
        toast("Guardando fotos…")
        worker.execute {
            val count = uris.take(40).count { photos.importPhoto(it) }
            handler.post {
                if (!isDestroyed && generation == pendingImport) {
                    toast("Se agregaron $count de ${uris.size} fotos (máximo 40, hasta 15 MB cada una)")
                    photoIndex = 0; nextPhoto()
                }
            }
        }
    }

    private fun nextPhoto() {
        if (photoBusy) return
        lastPhotoAt = System.currentTimeMillis()
        val sources = photos.sources()
        if (sources.isEmpty()) {
            photo.setImageResource(R.drawable.photo_empty); fullPhoto?.setImageDrawable(null)
            photoHint.text = "Nuestra familia\nOK para agregar fotos"
            return
        }
        val source = sources[Math.floorMod(photoIndex++, sources.size)]
        photoBusy = true
        val generation = photoGeneration
        worker.execute {
            val bitmap = photos.decode(source)
            handler.post {
                photoBusy = false
                if (!isDestroyed && generation == photoGeneration) {
                    photo.setImageBitmap(bitmap); fullPhoto?.setImageBitmap(bitmap)
                    photoHint.text = if (bitmap != null) "Nuestra familia" else "No se pudo leer esta foto"
                }
            }
        }
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    override fun onResume() {
        super.onResume()
        val apps = installed()
        val newKey = appsKey(apps)
        if (newKey != appSnapshotKey) {
            appSnapshotKey = newKey
            setContentView(buildUi(apps))
            lastPhotoAt = 0L
        }
        active = true; handler.removeCallbacks(ticker); handler.post(ticker)
    }
    override fun onPause() { active = false; handler.removeCallbacks(ticker); super.onPause() }
    override fun onDestroy() {
        pendingImport++; photoGeneration++; handler.removeCallbacksAndMessages(null)
        photoDialog?.dismiss(); worker.shutdown(); super.onDestroy()
    }
}

private class DesignSurface(context: android.content.Context, private val content: View) : ViewGroup(context) {
    init { addView(content) }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec); val h = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(w, h)
        content.measure(MeasureSpec.makeMeasureSpec(1920, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(1080, MeasureSpec.EXACTLY))
    }
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val scale = minOf(width / 1920f, height / 1080f)
        content.layout(0, 0, 1920, 1080)
        content.pivotX = 0f; content.pivotY = 0f
        content.scaleX = scale; content.scaleY = scale
        content.translationX = (width - 1920 * scale) / 2
        content.translationY = (height - 1080 * scale) / 2
    }
}


