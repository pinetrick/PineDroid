package com.pine.pinedroid.activity.image_pickup.pickup

import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.activity.image_pickup.camera.CameraScreenVM
import com.pine.pinedroid.activity.image_pickup.preview.ImagePreviewScreenVM
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImagePickupScreenVM : BaseViewModel() {
    private val _viewState = MutableStateFlow(ImagePickupScreenState())
    val viewState: StateFlow<ImagePickupScreenState> = _viewState

    fun onInit() {

        _viewState.update {
            it.copy(
                loading = false,
                enabledCamera = allowCamera,
                enabledMultiple = allowMultiple,
                imageUris = inputImages
            )
        }


    }

    fun onComplete() = viewModelScope.launch {
        _viewState.update {
            it.copy(
                loading = true
            )
        }

        callback?.invoke(_viewState.value.selectedImages)
    }

    fun onImageClicked(oneImage: OneImage) {
        ImagePreviewScreenVM.images = listOf(oneImage)
        navigateTo("preview")

    }

    fun onTakePhoto(oneImage: OneImage) {

        CameraScreenVM.allowFlash = true
        CameraScreenVM.callback = { oneImage ->
            oneImage?.let { oneImage ->

                val imageUris = listOf(oneImage) + _viewState.value.imageUris
                val choiceImages = listOf(oneImage) + _viewState.value.selectedImages

                // 使用协程确保在主线程更新
                viewModelScope.launch {
                    _viewState.update {
                        it.copy(
                            imageUris = imageUris,
                            selectedImages = choiceImages
                        )
                    }
                }


            }


        }
        navigateTo("camera")


    }


    fun onSelectChange(oneImage: OneImage) {
        val selectedImages = _viewState.value.selectedImages.toMutableList()
        if (!_viewState.value.enabledMultiple) { //单选
            selectedImages.clear()
        }

        if (selectedImages.contains(oneImage)) {
            selectedImages.remove(oneImage)
        } else {
            selectedImages.add(oneImage)
        }
        _viewState.update {
            it.copy(selectedImages = selectedImages.toList())
        }


    }

    companion object {
        var inputImages: List<OneImage> = emptyList()
        var allowCamera: Boolean = true
        var allowMultiple: Boolean = true
        var allowVideo: Boolean = true

        var callback: (suspend (List<OneImage>) -> Unit)? = null

    }
}