package com.pine.pinedroid.activity.image_pickup.pickup

import android.os.Build
import android.util.Size
import androidx.lifecycle.viewModelScope
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.activity.image_pickup.camera.CameraScreenVM
import com.pine.pinedroid.activity.image_pickup.preview.ImagePreviewScreenVM
import com.pine.pinedroid.file.image.Gallery.getGalleryImages
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import com.pine.pinedroid.utils.appContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImagePickupScreenVM : BaseViewModel<ImagePickupScreenState>(ImagePickupScreenState::class) {

    fun onInit() {
        if (inputImages.isEmpty()) {
            inputImages = getGalleryImages().map {
                OneImage.UriImage(it)
            }
        }


        setState {
            copy(
                loading = false,
                enabledCamera = allowCamera,
                maxCount = ImagePickupScreenVM.maxCount,
                imageUris = inputImages
            )
        }


    }

    override fun onReturnClick() {
        super.onReturnClick()
        _viewState.update {
            it.copy(
                selectedImages = emptyList()
            )
        }
        onComplete()
    }

    fun onComplete() = viewModelScope.launch {
        _viewState.update {
            it.copy(
                loading = true
            )
        }

        callback?.invoke(_viewState.value.selectedImages)

        cleanUp()
        navigateBack()
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
        var maxCount: Int = 9
        var allowVideo: Boolean = true

        var callback: (suspend (List<OneImage>) -> Unit)? = null

        fun cleanUp(){
            inputImages = emptyList()
            maxCount = 9
            allowCamera = true
            allowVideo = true
            callback = null

        }
    }
}