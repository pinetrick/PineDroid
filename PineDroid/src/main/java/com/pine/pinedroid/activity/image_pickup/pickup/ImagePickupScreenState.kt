package com.pine.pinedroid.activity.image_pickup.pickup

import com.pine.pinedroid.activity.image_pickup.OneImage


data class ImagePickupScreenState(
    val loading: Boolean = true,
    val enabledCamera: Boolean = false,
    val maxCount: Int = 9,
    val selectedImages: List<OneImage> = emptyList(),
    val imageUris: List<OneImage> = emptyList()
)