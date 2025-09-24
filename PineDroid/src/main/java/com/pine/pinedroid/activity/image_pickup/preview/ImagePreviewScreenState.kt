package com.pine.pinedroid.activity.image_pickup.preview

import com.pine.pinedroid.activity.image_pickup.OneImage

data class ImagePreviewScreenState(
    val images: List<OneImage> = emptyList(),
    val currentIndex: Int = 0
)