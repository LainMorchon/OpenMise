package com.morchon.lain.ui.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File

class AndroidCameraManager(
    private val context: Context,
    private val savedUriState: MutableState<String?>,
    private val galleryLauncher: (PickVisualMediaRequest) -> Unit,
    private val cameraLauncher: (Uri) -> Unit,
    private val permissionLauncher: (String) -> Unit
) : CameraManager {

    override fun capturarFoto() {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            launchCameraInternal()
        } else {
            permissionLauncher(Manifest.permission.CAMERA)
        }
    }

    private fun launchCameraInternal() {
        try {
            val directory = File(context.cacheDir, "images")
            if (!directory.exists()) directory.mkdirs()
            
            val file = File(directory, "camera_tmp.jpg")
            if (file.exists()) file.delete()
            file.createNewFile()
            
            // Usamos la autoridad exacta definida en el Manifest
            val authority = "com.morchon.lain.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, file)
            
            savedUriState.value = uri.toString()
            cameraLauncher(uri)
        } catch (e: Exception) {
            android.util.Log.e("CameraManager", "Error al iniciar cámara: ${e.message}")
        }
    }

    override fun seleccionarDeGaleria() {
        galleryLauncher(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    companion object {
        fun processImage(context: Context, uri: Uri): ByteArray? {
            return try {
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri)?.use { 
                    BitmapFactory.decodeStream(it, null, options) 
                }

                val reqSize = 1024
                var inSampleSize = 1
                if (options.outHeight > reqSize || options.outWidth > reqSize) {
                    val halfHeight = options.outHeight / 2
                    val halfWidth = options.outWidth / 2
                    while (halfHeight / inSampleSize >= reqSize && halfWidth / inSampleSize >= reqSize) {
                        inSampleSize *= 2
                    }
                }

                val finalOptions = BitmapFactory.Options().apply {
                    this.inSampleSize = inSampleSize
                    inJustDecodeBounds = false
                }
                
                var bitmap = context.contentResolver.openInputStream(uri)?.use { 
                    BitmapFactory.decodeStream(it, null, finalOptions) 
                } ?: return null

                bitmap = rotateImageIfRequired(context, bitmap, uri)

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val result = outputStream.toByteArray()
                
                bitmap.recycle()
                result
            } catch (e: Exception) {
                null
            }
        }

        private fun rotateImageIfRequired(context: Context, bitmap: Bitmap, uri: Uri): Bitmap {
            return try {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return bitmap
                val ei = ExifInterface(inputStream)
                val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                inputStream.close()

                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                    else -> bitmap
                }
            } catch (e: Exception) {
                bitmap
            }
        }

        private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(degrees)
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (rotated != bitmap) bitmap.recycle()
            return rotated
        }
    }
}

@Composable
actual fun rememberCameraManager(onImagePicked: (ByteArray?) -> Unit): CameraManager {
    val context = LocalContext.current
    val savedUriState = rememberSaveable { mutableStateOf<String?>(null) }
    val currentOnImagePicked by rememberUpdatedState(onImagePicked)

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val bytes = AndroidCameraManager.processImage(context, it)
            currentOnImagePicked(bytes)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            savedUriState.value?.let { uriString ->
                val uri = Uri.parse(uriString)
                val bytes = AndroidCameraManager.processImage(context, uri)
                currentOnImagePicked(bytes)
            }
        }
    }

    // Usamos una referencia para poder llamar a capturarFoto tras el permiso
    var cameraManagerRef by remember { mutableStateOf<AndroidCameraManager?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraManagerRef?.capturarFoto()
        }
    }

    val cameraManager = remember {
        AndroidCameraManager(
            context = context,
            savedUriState = savedUriState,
            galleryLauncher = { galleryLauncher.launch(it) },
            cameraLauncher = { cameraLauncher.launch(it) },
            permissionLauncher = { permissionLauncher.launch(it) }
        )
    }.also { cameraManagerRef = it }

    return cameraManager
}
