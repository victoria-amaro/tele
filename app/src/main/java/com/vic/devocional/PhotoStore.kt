package com.vic.devocional

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import java.io.File
import java.io.InputStream

/** Copies user-selected images into app-private storage; never changes the originals. */
class PhotoStore(private val context: Context) {
    private val directory = File(context.filesDir, "photos").apply { mkdirs() }
    private val extensions = setOf("jpg", "jpeg", "png", "webp")
    fun sources(): List<String> {
        val bundled = context.assets.list("photos").orEmpty()
            .filter { it.substringAfterLast('.', "").lowercase() in extensions }.sorted().map { "asset:$it" }
        val imported = directory.listFiles().orEmpty().filter { it.extension == "photo" }.sortedBy { it.name }.map { it.absolutePath }
        return bundled + imported
    }
    private fun open(source: String): InputStream = if (source.startsWith("asset:")) {
        context.assets.open("photos/${source.removePrefix("asset:")}")
    } else File(source).inputStream()

    fun decode(source: String): Bitmap? = try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        open(source).use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) null else {
            var sample = 1
            while (bounds.outWidth / sample > 2048 || bounds.outHeight / sample > 1152) sample *= 2
            val options = BitmapFactory.Options().apply { inSampleSize = sample }
            val bitmap = open(source).use { BitmapFactory.decodeStream(it, null, options) }
            val orientation = try {
                open(source).use { ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL) }
            } catch (_: Exception) { ExifInterface.ORIENTATION_NORMAL }
            if (bitmap == null) null else rotate(bitmap, orientation)
        }
    } catch (_: Exception) { null }

    private fun rotate(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.setScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> { matrix.setRotate(90f); matrix.postScale(-1f, 1f) }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> { matrix.setRotate(270f); matrix.postScale(-1f, 1f) }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
            else -> return bitmap
        }
        val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (result !== bitmap) bitmap.recycle()
        return result
    }

    fun importPhoto(uri: Uri): Boolean {
        if (directory.listFiles().orEmpty().count { it.extension == "photo" } >= 40) return false
        val temporary = File.createTempFile("import-", ".tmp", directory)
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                temporary.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var total = 0L
                    while (true) {
                        val read = input.read(buffer)
                        if (read < 0) break
                        total += read
                        require(total <= 15L * 1024 * 1024) { "Foto demasiado grande" }
                        output.write(buffer, 0, read)
                    }
                }
            } ?: return false
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(temporary.absolutePath, bounds)
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) false
            else temporary.renameTo(File(directory, "${java.util.UUID.randomUUID()}.photo"))
        } catch (_: Exception) { false } finally { temporary.delete() }
    }
    fun clear() { directory.listFiles().orEmpty().forEach { it.delete() } }
}


