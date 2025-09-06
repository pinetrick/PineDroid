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
                images = ImagePreviewScreenVM.images
            )
        }


    }

    companion object {
        var images: List<OneImage> = emptyList() //传入被预览的图片
    }
}