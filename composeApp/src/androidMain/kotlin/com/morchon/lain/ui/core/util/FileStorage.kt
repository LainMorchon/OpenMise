package com.morchon.lain.ui.core.util

import android.content.Context
import java.io.File
import java.util.UUID

class FileStorage(private val context: Context) {
    fun guardarImagenInterna(bytes: ByteArray): String? {
        return try {
            val nombreArchivo = "receta_${UUID.randomUUID()}.jpg"
            val archivo = File(context.filesDir, nombreArchivo)
            archivo.writeBytes(bytes)
            archivo.absolutePath // Devolvemos la ruta completa
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
