package com.pine.pinedroid.activity.image_pickup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import com.pine.pinedroid.activity.image_pickup.pickup.ImagePickupScreenVM
import com.pine.pinedroid.utils.log.loge

class ImagePickupActivity : ComponentActivity() {
    val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        // 照片拍摄成功，处理图片
        if (onPhotoToken == null) return@registerForActivityResult loge("Please set onPhotoToken first")
        onPhotoToken?.invoke(success)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initScreen = intent.getStringExtra("initScreen") ?: "pickup"

        setContent {
            MaterialTheme {
                RequirePermissionForImagePickUp(initScreen, cameraLauncher)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        ImagePickupScreenVM.inputImages = emptyList()
    }

    companion object {
        var onPhotoToken: ((isSuccess: Boolean) -> Unit)? = null
    }
}
