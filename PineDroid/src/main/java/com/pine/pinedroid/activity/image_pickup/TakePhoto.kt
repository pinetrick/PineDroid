package com.pine.pinedroid.activity.image_pickup

import android.content.Intent
import com.pine.pinedroid.activity.image_pickup.camera.CameraScreenVM
import com.pine.pinedroid.activity.image_pickup.pickup.ImagePickupScreenVM
import com.pine.pinedroid.file.image.Gallery.getGalleryImages
import com.pine.pinedroid.utils.currentActivity

object TakePhoto {


    fun takePhoto(
        allowFlash: Boolean = false,
        callback: (suspend (OneImage?) -> Unit)?
    ) {
        CameraScreenVM.allowFlash = allowFlash
        CameraScreenVM.callback = callback


        val intent = Intent(currentActivity, ImagePickupActivity::class.java).apply {
            putExtra("initScreen", "pickup")
        }


        currentActivity.startActivity(intent)

        // 使用回调机制返回结果
        CameraScreenVM.callback = callback
    }


}