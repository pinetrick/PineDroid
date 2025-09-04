package com.pine.pinedroid.activity.image_pickup.pickup

import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.activity.image_pickup.preview.ImagePreviewScreenVM
import com.pine.pinedroid.file.image.Gallery.getGalleryImages
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ImagePickupScreenVM : BaseViewModel() {
    private val _viewState = MutableStateFlow(ImagePickupScreenState())
    val viewState: StateFlow<ImagePickupScreenState> = _viewState

    fun onInit(allowCamera: Boolean, allowMultiple: Boolean) {
        val pictureUris = getGalleryImages().map { OneImage.UriImage(it) }
        _viewState.update {
            it.copy(
                enabledCamera = allowCamera,
                enabledMultiple = allowMultiple,
                imageUris = pictureUris.toList()
            )
        }


    }

    fun onComplete(){

    }

    fun onImageClicked(oneImage: OneImage) {
        ImagePreviewScreenVM.images = listOf(oneImage)
        navigateTo("preview")

    }

    fun onTakePhoto(oneImage: OneImage) {


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
}