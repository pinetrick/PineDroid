package com.pine.pinedroid.activity.image_pickup.preview

import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.utils.shrinker_keep.Keep

@Keep
data class ImagePreviewScreenState(
    val images: List<OneImage> = emptyList(),
    val currentIndex: Int = 0
)