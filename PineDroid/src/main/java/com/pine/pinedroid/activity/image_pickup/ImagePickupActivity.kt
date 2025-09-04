package com.pine.pinedroid.activity.image_pickup

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class ImagePickupActivity : ComponentActivity() {
    companion object {
        var onResultCallback: ((List<Uri>) -> Unit)? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val allowCamera = intent.getBooleanExtra("allowCamera", true)
        val allowMultiple = intent.getBooleanExtra("allowMultiple", true)

        setContent {
            MaterialTheme {
                RequirePermissionForImagePickUp(allowCamera, allowMultiple)


            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onResultCallback = null
    }
}
