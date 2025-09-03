package com.pine.pinedroid.activity.image_pickup


import android.content.Intent
import android.net.Uri
import com.pine.pinedroid.utils.currentActivity

object ImagePickup {
    var result: MutableList<Uri> = mutableListOf()


    fun pickImageFromGallery(
        allowCamera: Boolean = true,
        allowMultiple: Boolean = true,
        callback: (List<Uri>) -> Unit
    ) {
        result.clear()

        val intent = Intent(currentActivity, ImagePickupActivity::class.java).apply {
            putExtra("allowCamera", allowCamera)
            putExtra("allowMultiple", allowMultiple)
        }

        currentActivity.startActivity(intent)

        // 使用回调机制返回结果
        ImagePickupActivity.onResultCallback = callback
    }


}