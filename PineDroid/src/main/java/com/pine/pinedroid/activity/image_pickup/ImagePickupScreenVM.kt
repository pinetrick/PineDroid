package com.pine.pinedroid.activity.image_pickup

import androidx.core.net.toUri
import com.pine.pinedroid.jetpack.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ImagePickupScreenVM: BaseViewModel() {
    private val _viewState = MutableStateFlow(ImagePickupScreenState())
    val viewState: StateFlow<ImagePickupScreenState> = _viewState

    fun onInit(){
        val sampleUris = mutableListOf(
            "content://media/external/images/media/1",
            "content://media/external/images/media/2",
            "content://media/external/images/media/3",
            "content://media/external/images/media/4",
            "content://media/external/images/media/5",
            "content://media/external/images/media/6",
            "content://media/external/images/media/7",
            "content://media/external/images/media/8",
            "content://media/external/images/media/9",
            "content://media/external/images/media/10",
            "content://media/external/images/media/11",
            "content://media/external/images/media/12"
        ).map { it.toUri() }.toMutableList()

        _viewState.update {
            it.copy(imageUris = sampleUris)

        }

    }
}