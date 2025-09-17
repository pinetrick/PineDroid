package com.pine.pinedroid.activity.image_pickup

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pine.pinedroid.activity.image_pickup.camera.CameraScreen
import com.pine.pinedroid.activity.image_pickup.pickup.ImagePickupScreen
import com.pine.pinedroid.activity.image_pickup.preview.ImagePreviewScreen


@Composable
fun ImagePickupNav(
    initScreen: String,
    cameraLauncher: ActivityResultLauncher<Uri>
) {
    val navController = rememberNavController()

    // 2. Scaffold 包裹页面，可加入顶部栏/底部栏
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        // 3. NavHost 管理不同 Composable 页面
        NavHost(
            navController = navController,
            startDestination = initScreen,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("pickup") { ImagePickupScreen(navController) }
            composable("preview") { ImagePreviewScreen(navController) }
            composable("camera") { CameraScreen(navController, cameraLauncher) }

        }
    }
}