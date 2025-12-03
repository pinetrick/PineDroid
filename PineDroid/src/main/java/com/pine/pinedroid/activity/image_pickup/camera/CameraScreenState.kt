package com.pine.pinedroid.activity.image_pickup.camera

import androidx.camera.core.ImageCapture.FLASH_MODE_AUTO
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.utils.shrinker_keep.Keep

@Keep
data class CameraScreenState(
    val allowFlash: Boolean = false,
    val flashMode: Int = FLASH_MODE_AUTO,
    val isFrontCamera: Boolean = false,
    val cameraPhoto: OneImage? = null,

    val useSystemCamera: Boolean = true,
    val handlingPicture: Boolean = false,

)