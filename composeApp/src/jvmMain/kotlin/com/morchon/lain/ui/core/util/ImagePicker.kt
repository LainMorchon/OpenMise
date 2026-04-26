package com.morchon.lain.ui.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

actual class ImagePicker(private val onImagePicked: (ByteArray?) -> Unit) {
    @Composable
    actual fun registerPicker(onImagePicked: (ByteArray?) -> Unit) {
        // No necesita registro especial en Desktop (Swing)
    }

    actual fun pickImage() {
        val fileChooser = JFileChooser().apply {
            fileFilter = FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png", "webp")
            dialogTitle = "Seleccionar imagen de receta"
        }
        val result = fileChooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val file = fileChooser.selectedFile
            onImagePicked(file.readBytes())
        }
    }

    actual fun takePicture() {
        // La cámara en Desktop es compleja sin librerías externas. 
        // Por ahora redirigimos a selección de archivo.
        pickImage()
    }
}

@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray?) -> Unit): ImagePicker {
    return remember { ImagePicker(onImagePicked) }
}
