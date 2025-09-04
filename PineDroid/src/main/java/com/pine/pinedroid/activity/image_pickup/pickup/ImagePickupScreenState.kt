package com.pine.pinedroid.activity.image_pickup.pickup

import com.pine.pinedroid.activity.image_pickup.OneImage


data class ImagePickupScreenState(
    val enabledCamera: Boolean = false,
    val enabledMultiple: Boolean = false,
    val selectedImages: List<OneImage> = emptyList(),
    val imageUris: List<OneImage> = emptyList()
)