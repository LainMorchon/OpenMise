package com.morchon.lain.ui.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

actual class ImagePicker(private val onImagePicked: (ByteArray?) -> Unit) {
    @Composable
    actual fun registerPicker(onImagePicked: (ByteArray?) -> Unit) {
        // Implementación pendiente para iOS (requiere UIViewController nativo)
    }

    actual fun pickImage() {
        // Por ahora vacío hasta integrar PHPickerViewController
    }

    actual fun takePicture() {
        // Por ahora vacío hasta integrar UIImagePickerController
    }
}

@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray?) -> Unit): ImagePicker {
    return remember { ImagePicker(onImagePicked) }
}
