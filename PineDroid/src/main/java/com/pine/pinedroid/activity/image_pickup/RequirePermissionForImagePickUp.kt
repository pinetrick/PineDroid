package com.pine.pinedroid.activity.image_pickup

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pine.pinedroid.activity.db_selection.DbSelection
import com.pine.pinedroid.activity.image_pickup.camera.CameraScreen
import com.pine.pinedroid.activity.image_pickup.camera.CameraScreenState
import com.pine.pinedroid.activity.image_pickup.pickup.ImagePickupScreen
import com.pine.pinedroid.activity.image_pickup.pickup.ImagePickupScreenVM
import com.pine.pinedroid.activity.image_pickup.preview.ImagePreviewScreen
import com.pine.pinedroid.activity.sql.RunSqlScreen
import com.pine.pinedroid.activity.table_selection.TableSelection
import com.pine.pinedroid.activity.text_editor.TextEditorScreen
import com.pine.pinedroid.jetpack.ui.require_permission.rememberPermissionController

@Composable
fun RequirePermissionForImagePickUp(initScreen: String) {
    // 构建正确的权限列表
    val requiredPermissions = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ 用新的媒体权限
            add(Manifest.permission.READ_MEDIA_IMAGES)
            // 如果还需要视频/音频，加上：
            add(Manifest.permission.READ_MEDIA_VIDEO)
            add(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            // Android 12 及以下，还是用旧的存储权限
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        // 相机权限（如果需要）
        if (ImagePickupScreenVM.allowCamera) {
            add(Manifest.permission.CAMERA)
        }
    }

    var hasPermission by remember { mutableStateOf<Boolean?>(null) }

    val permissionController = rememberPermissionController(
        permissions = requiredPermissions,
        onGranted = {
            hasPermission = true

        },
        onDenied = {
            hasPermission = false
            it.requestPermissions()
        },
        onShowRationale = {
            hasPermission = null
        },
    )


    when (hasPermission) {
        true -> {
            ImagePickupScaffold(initScreen)
        }

        false -> {
            Button(onClick = { permissionController.requestPermissions() }) {
                Text("请求权限")
            }
        }

        null -> {
            Button(onClick = { permissionController.requestPermissions() }) {
                Text("永久被拒绝")
            }
        }
    }
}

@Composable
fun ImagePickupScaffold(
    initScreen: String
){
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
            composable("pickup") { ImagePickupScreen(navController, onBack = { navController.popBackStack() }) }
            composable("preview") { ImagePreviewScreen(navController) }
            composable("camera") { CameraScreen(navController) }

        }
    }
}