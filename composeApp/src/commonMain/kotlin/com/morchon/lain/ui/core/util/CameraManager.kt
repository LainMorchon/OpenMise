package com.morchon.lain.ui.core.util

import androidx.compose.runtime.Composable

interface CameraManager {
    fun capturarFoto()
    fun seleccionarDeGaleria()
}

@Composable
expect fun rememberCameraManager(onImagePicked: (ByteArray?) -> Unit): CameraManager
