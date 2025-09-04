package com.pine.pinedroid.activity.image_pickup.camera

import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.activity.image_pickup.preview.ImagePreviewScreenState
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
class CameraScreenVM : BaseViewModel() {
    private val _viewState = MutableStateFlow(CameraScreenState())
    val viewState: StateFlow<CameraScreenState> = _viewState

    fun onInit() {
        _viewState.update { currentState ->
            currentState.copy(
                allowFlash = allowFlash
            )
        }
    }

    fun toggleFlash() {
        _viewState.update { currentState ->
            currentState.copy(
                flashOn = !currentState.flashOn
            )
        }
    }

    fun switchCamera() {
        // 切换前后摄像头逻辑
        // 这里需要实现实际的摄像头切换
    }


    fun onClose() {
        navigateBack()
    }

    companion object {
        var allowFlash: Boolean = true
        var callback: (suspend (OneImage?) -> Unit)? = null
    }
}