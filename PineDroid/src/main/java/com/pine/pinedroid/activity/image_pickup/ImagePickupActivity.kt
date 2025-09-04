package com.pine.pinedroid.activity.image_pickup

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.pine.pinedroid.activity.image_pickup.pickup.ImagePickupScreenVM

class ImagePickupActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initScreen = intent.getStringExtra("initScreen") ?: "pickup"

        setContent {
            MaterialTheme {
                RequirePermissionForImagePickUp(initScreen)


            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ImagePickupScreenVM.inputImages = emptyList()
    }
}
