package com.pine.pinedroid.activity.image_pickup.camera

import com.pine.pinedroid.activity.image_pickup.OneImage

// 更新 State 类
data class CameraScreenState(
    val allowFlash: Boolean = false,
    val flashOn: Boolean = false,
    val isFrontCamera: Boolean = false // 添加摄像头状态
)