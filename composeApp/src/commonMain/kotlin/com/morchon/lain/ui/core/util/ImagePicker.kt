package com.morchon.lain.ui.core.util

import androidx.compose.runtime.Composable

/**
 * Interfaz para abstraer la selección de imágenes (Cámara/Galería) en KMP.
 */
expect class ImagePicker {
    @Composable
    fun registerPicker(onImagePicked: (ByteArray?) -> Unit)

    fun pickImage()
    fun takePicture()
}

@Composable
expect fun rememberImagePicker(onImagePicked: (ByteArray?) -> Unit): ImagePicker
