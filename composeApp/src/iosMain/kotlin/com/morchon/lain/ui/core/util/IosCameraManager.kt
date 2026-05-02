package com.morchon.lain.ui.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import platform.UIKit.*
import platform.Foundation.*
import platform.posix.memcpy

class IosCameraManager(
    private val onImagePicked: (ByteArray?) -> Unit
) : CameraManager {

    private val imagePickerController = UIImagePickerController().apply {
        setSourceType(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)
        setDelegate(object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
            @OptIn(ExperimentalForeignApi::class)
            override fun imagePickerController(
                picker: UIImagePickerController,
                didFinishPickingMediaWithInfo: Map<Any?, *>
            ) {
                val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
                val bytes = image?.let { uiImageToByteArray(it) }
                onImagePicked(bytes)
                picker.dismissViewControllerAnimated(true, null)
            }

            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                onImagePicked(null)
                picker.dismissViewControllerAnimated(true, null)
            }
        })
    }

    override fun capturarFoto() {
        if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            rootViewController?.presentViewController(imagePickerController, true, null)
        }
    }

    override fun seleccionarDeGaleria() {
        val galleryPicker = UIImagePickerController().apply {
            setSourceType(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary)
            setDelegate(imagePickerController.delegate)
        }
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(galleryPicker, true, null)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun uiImageToByteArray(image: UIImage): ByteArray? {
        val jpegData = UIImageJPEGRepresentation(image, 0.8) ?: return null
        val length = jpegData.length.toInt()
        val bytes = ByteArray(length)
        if (length > 0) {
            memcpy(bytes.refTo(0), jpegData.bytes, jpegData.length)
        }
        return bytes
    }
}

@Composable
actual fun rememberCameraManager(onImagePicked: (ByteArray?) -> Unit): CameraManager {
    return remember { IosCameraManager(onImagePicked) }
}
