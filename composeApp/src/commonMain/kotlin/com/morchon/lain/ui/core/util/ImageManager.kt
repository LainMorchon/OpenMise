package com.morchon.lain.ui.core.util

/**
 * Interfaz para gestionar la persistencia de imágenes en KMP.
 */
interface ImageManager {
    /**
     * Guarda un ByteArray como imagen y devuelve la ruta/URL.
     */
    fun saveImage(bytes: ByteArray): String?
}
