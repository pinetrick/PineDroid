package com.pine.pindroidpp.root

import com.pine.pinedroid.activity.image_pickup.ImagePickup
import com.pine.pinedroid.activity.image_pickup.preview.ImagePreviewScreenVM
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


class RootScreenVM : BaseViewModel() {
    private val _viewState = MutableStateFlow(RootScreenState())
    val viewState: StateFlow<RootScreenState> = _viewState

    fun onInit() {

    }

    fun onTakePhoto() {
        ImagePickup.takePhoto(
            allowVideo = true,
            useSystemCamera = true,
        ) { oneImage ->

        }
    }

    fun onImagePickUp() {
        ImagePickup.pickImageFromGallery(
            allowCamera = true,
            allowMultiple = true,
            allowVideo = true,
            useSystemCamera = true,
        ) { oneImage ->

        }
    }
}