package com.pine.pinedroid.activity.image_pickup.preview

import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.activity.image_pickup.pickup.ImagePickupScreenState
import com.pine.pinedroid.file.image.Gallery.getGalleryImages
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ImagePreviewScreenVM: BaseViewModel<ImagePreviewScreenState>(ImagePreviewScreenState::class) {

    fun onInit() {
        _viewState.update { currentState ->
            currentState.copy(
                images = ImagePreviewScreenVM.images,
                currentIndex = index // 默认显示第一张
            )
        }
    }

    fun onSwipeToIndex(index: Int) {
        _viewState.update { currentState ->
            currentState.copy(
                currentIndex = index
            )
        }
    }

    fun navigateToNext() {
        _viewState.update { currentState ->
            val nextIndex = (currentState.currentIndex + 1) % currentState.images.size
            currentState.copy(currentIndex = nextIndex)
        }
    }

    fun navigateToPrevious() {
        _viewState.update { currentState ->
            val prevIndex = if (currentState.currentIndex - 1 < 0) {
                currentState.images.size - 1
            } else {
                currentState.currentIndex - 1
            }
            currentState.copy(currentIndex = prevIndex)
        }
    }

    companion object {
        var images: List<OneImage> = emptyList() //传入被预览的图片
        var index: Int = 0
    }
}