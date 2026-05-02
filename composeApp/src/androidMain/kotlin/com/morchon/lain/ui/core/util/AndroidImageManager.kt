package com.morchon.lain.ui.core.util

import android.content.Context
import java.io.File
import java.util.UUID

class AndroidImageManager(private val context: Context) : ImageManager {
    override fun saveImage(bytes: ByteArray): String? {
        return try {
            val directory = File(context.filesDir, "recetas_images")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = "receta_${UUID.randomUUID()}.jpg"
            val file = File(directory, fileName)
            file.writeBytes(bytes)
            "file://${file.absolutePath}"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
