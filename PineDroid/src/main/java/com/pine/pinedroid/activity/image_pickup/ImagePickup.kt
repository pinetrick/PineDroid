package com.pine.pinedroid.activity.image_pickup


import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import com.pine.pinedroid.activity.image_pickup.camera.CameraScreen
import com.pine.pinedroid.activity.image_pickup.camera.CameraScreenVM
import com.pine.pinedroid.activity.image_pickup.pickup.ImagePickupScreenVM
import com.pine.pinedroid.file.image.Gallery.getGalleryImages
import com.pine.pinedroid.utils.activityContext
import com.pine.pinedroid.utils.currentActivity

object ImagePickup {


    fun pickImageFromGallery(
        allowCamera: Boolean = true,
        allowMultiple: Boolean = true,
        allowVideo: Boolean = true,
        useSystemCamera: Boolean = true,
        callback: suspend (List<OneImage>) -> Unit
    ) {
        ImagePickupScreenVM.allowCamera = allowCamera
        ImagePickupScreenVM.allowMultiple = allowMultiple
        ImagePickupScreenVM.allowVideo = allowVideo
        ImagePickupScreenVM.inputImages = getGalleryImages().map { OneImage.UriImage(it) }
        CameraScreenVM.useSystemCamera = useSystemCamera

        val intent = Intent(currentActivity, ImagePickupActivity::class.java).apply {
            putExtra("initScreen", "pickup")
        }

        currentActivity.startActivity(intent)

        // 使用回调机制返回结果
        ImagePickupScreenVM.callback = {
            callback(it)
            if (activityContext is ImagePickupActivity) {
                (activityContext as ImagePickupActivity).finish()
            }
        }
    }

    fun takePhoto(
        allowVideo: Boolean = true,
        useSystemCamera: Boolean = true,
        callback: suspend (OneImage?) -> Unit
    ) {
        ImagePickupScreenVM.allowVideo = allowVideo
        CameraScreenVM.useSystemCamera = useSystemCamera

        val intent = Intent(currentActivity, ImagePickupActivity::class.java).apply {
            putExtra("initScreen", "camera")
        }


        currentActivity.startActivity(intent)

        // 使用回调机制返回结果
        CameraScreenVM.callback = {
            callback(it)
            if (activityContext is ImagePickupActivity) {
                (activityContext as ImagePickupActivity).finish()
            }
        }
    }

}