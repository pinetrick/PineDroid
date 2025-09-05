package com.pine.pinedroid.activity.image_pickup.camera

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_AUTO
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.activity.image_pickup.ImagePickupActivity
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.activity.image_pickup.pickup.ImagePickupScreenVM
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.utils.appContext
import com.pine.pinedroid.utils.log.logd
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraScreenVM : BaseViewModel() {
    private val _viewState = MutableStateFlow(CameraScreenState())
    val viewState: StateFlow<CameraScreenState> = _viewState

    var cameraLauncher: ActivityResultLauncher<Uri>? = null
    fun onInit(cameraLauncher: ActivityResultLauncher<Uri>?) {
        this.cameraLauncher = cameraLauncher

        if (useSystemCamera) {
            ImagePickupActivity.onPhotoToken = ::handleImage
            launchCamera()
        }


        _viewState.update { currentState ->
            currentState.copy(
                useSystemCamera = useSystemCamera,
                allowFlash = allowFlash,
                flashMode = if (allowFlash) FLASH_MODE_AUTO else FLASH_MODE_OFF
            )
        }
    }



    fun confirmPicture() = viewModelScope.launch {

        callback?.invoke(_viewState.value.cameraPhoto)
        callback = null

        navigateBack()
    }

    //从新拍照
    fun retry() {
        _viewState.update { currentState ->
            currentState.copy(
                cameraPhoto = null
            )
        }
    }

    fun toggleFlash() {
        _viewState.update { currentState ->
            currentState.copy(
                flashMode = if (currentState.flashMode == 2) 0 else currentState.flashMode + 1
            )
        }
    }

    fun takePicture(imageCapture: ImageCapture?) {

        val file = File(
            appContext.cacheDir,
            "${System.currentTimeMillis()}.jpg"
        )


        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(appContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    logd("CameraScreen", "拍照失败: ${exc.message}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    logd("CameraScreen", "保存成功: ${file.absolutePath}")

                    _viewState.update { currentState ->
                        currentState.copy(
                            cameraPhoto = OneImage.LocalImage(file.absolutePath)
                        )
                    }

                }
            }
        )
    }

    fun switchCamera() {
        _viewState.update { currentState ->
            currentState.copy(
                isFrontCamera = !currentState.isFrontCamera
            )
        }
    }




    var currentPhotoPath: String? = null
    private fun launchCamera() {
        val photoFile = createImageFile()
        val photoURI = FileProvider.getUriForFile(
            appContext,
            "${appContext.packageName}.fileprovider",
            photoFile
        )
        cameraLauncher?.launch(photoURI)
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = appContext.cacheDir // 使用内部缓存目录
        // 或者使用外部缓存目录（如果可用）：
        // val storageDir: File? = externalCacheDir

        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun handleImage() = viewModelScope.launch {
        if (currentPhotoPath == null) {
            callback?.invoke(null)
        }
        else{
            callback?.invoke(OneImage.LocalImage(currentPhotoPath!!))
        }
        navigateBack()

    }

    companion object {
        var useSystemCamera = true
        var allowFlash: Boolean = true
        var callback: (suspend (OneImage?) -> Unit)? = null
    }
}