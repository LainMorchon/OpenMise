package com.morchon.lain.ui.core.util

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.util.UUID

actual class ImagePicker(
    private val context: Context,
    private val onImagePicked: (ByteArray?) -> Unit
) {
    private var imageUri: Uri? = null
    private var galleryLauncher: androidx.activity.result.ActivityResultLauncher<PickVisualMediaRequest>? = null
    private var cameraLauncher: androidx.activity.result.ActivityResultLauncher<Uri>? = null

    @Composable
    actual fun registerPicker(onImagePicked: (ByteArray?) -> Unit) {
        galleryLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                val bytes = context.contentResolver.openInputStream(it)?.readBytes()
                onImagePicked(bytes)
            }
        }

        cameraLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                imageUri?.let { uri ->
                    val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                    onImagePicked(bytes)
                }
            }
        }
    }

    actual fun pickImage() {
        galleryLauncher?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    actual fun takePicture() {
        val file = File(context.cacheDir, "camera_image_${UUID.randomUUID()}.jpg")
        imageUri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        cameraLauncher?.launch(imageUri!!)
    }
}

@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray?) -> Unit): ImagePicker {
    val context = LocalContext.current
    val picker = remember { ImagePicker(context, onImagePicked) }
    picker.registerPicker(onImagePicked)
    return picker
}
