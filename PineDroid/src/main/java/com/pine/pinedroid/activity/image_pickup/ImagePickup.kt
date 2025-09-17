package com.pine.pinedroid.activity.image_pickup


import android.content.Intent
import com.pine.pinedroid.activity.image_pickup.camera.CameraScreenVM
import com.pine.pinedroid.activity.image_pickup.pickup.ImagePickupScreenVM
import com.pine.pinedroid.hardware.permission.PinePermissionUtils
import com.pine.pinedroid.hardware.permission.one_permission.PineOnePermissionCamera
import com.pine.pinedroid.hardware.permission.one_permission.PineOnePermissionReadExternalStorage
import com.pine.pinedroid.hardware.permission.one_permission.PineOnePermissionReadMediaImages
import com.pine.pinedroid.hardware.permission.one_permission.PineOnePermissionReadMediaVideo
import com.pine.pinedroid.utils.activityContext
import com.pine.pinedroid.utils.currentActivity

object ImagePickup {


    suspend fun pickImageFromGallery(
        allowCamera: Boolean = true,
        allowMultiple: Boolean = true,
        allowVideo: Boolean = true,
        useSystemCamera: Boolean = true,
        callback: suspend (List<OneImage>) -> Unit
    ) {
        ImagePickupScreenVM.allowCamera = allowCamera
        ImagePickupScreenVM.allowMultiple = allowMultiple
        ImagePickupScreenVM.allowVideo = allowVideo
        CameraScreenVM.useSystemCamera = useSystemCamera

        PinePermissionUtils.requestPermissions(
            listOfNotNull(
                PineOnePermissionReadExternalStorage(),
                if (allowCamera) PineOnePermissionCamera() else null,
                if (allowVideo) PineOnePermissionReadMediaVideo() else null,
                PineOnePermissionReadMediaImages()
            )
        ) {
            if (it) {
                val intent = Intent(currentActivity, ImagePickupActivity::class.java).apply {
                    putExtra("initScreen", "pickup")
                }
                currentActivity.startActivity(intent)
            }
        }


        // 使用回调机制返回结果
        ImagePickupScreenVM.callback = {
            callback(it)
            if (activityContext is ImagePickupActivity) {
                (activityContext as ImagePickupActivity).finish()
            }
        }
    }

    suspend fun takePhoto(
        allowVideo: Boolean = true,
        useSystemCamera: Boolean = true,
        callback: suspend (OneImage?) -> Unit
    ) {
        ImagePickupScreenVM.allowVideo = allowVideo
        CameraScreenVM.useSystemCamera = useSystemCamera

        PinePermissionUtils.requestPermissions(
            listOfNotNull(
                PineOnePermissionCamera()
            )
        ) {
            if (it) {

                val intent = Intent(currentActivity, ImagePickupActivity::class.java).apply {
                    putExtra("initScreen", "camera")
                }
                currentActivity.startActivity(intent)
            }
        }


        // 使用回调机制返回结果
        CameraScreenVM.callback = {
            callback(it)
            if (activityContext is ImagePickupActivity) {
                (activityContext as ImagePickupActivity).finish()
            }
        }
    }

}