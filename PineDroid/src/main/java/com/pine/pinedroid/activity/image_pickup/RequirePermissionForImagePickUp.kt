package com.pine.pinedroid.activity.image_pickup

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pine.pinedroid.R
import com.pine.pinedroid.activity.image_pickup.camera.CameraScreen
import com.pine.pinedroid.activity.image_pickup.pickup.ImagePickupScreen
import com.pine.pinedroid.activity.image_pickup.pickup.ImagePickupScreenVM
import com.pine.pinedroid.activity.image_pickup.preview.ImagePreviewScreen
import com.pine.pinedroid.jetpack.ui.loading.PineLoading
import com.pine.pinedroid.jetpack.ui.require_permission.rememberPermissionController
import com.pine.pinedroid.utils.r_resource.stringResource

enum class PermissionState {
    Unknown, Granted, Denied, DeniedPermanently
}

@Composable
fun RequirePermissionForImagePickUp(
    initScreen: String,
    cameraLauncher: ActivityResultLauncher<Uri>
) {
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

    var hasPermission by remember { mutableStateOf<PermissionState>(PermissionState.Unknown) }

    val permissionController = rememberPermissionController(
        permissions = requiredPermissions,
        onGranted = {
            hasPermission = PermissionState.Granted

        },
        onDenied = {
            hasPermission = PermissionState.Denied
            it.requestPermissions()
        },
        onShowRationale = {
            hasPermission = PermissionState.DeniedPermanently
        },
    )


    when (hasPermission) {
        PermissionState.Granted -> {
            ImagePickupScaffold(initScreen, cameraLauncher)
        }

        PermissionState.Denied -> {
            PermissionExplanationCard(
                title = R.string.pine_image_permission_denied_title.stringResource(),
                description = R.string.pine_image_permission_denied_subtitle.stringResource(),
                buttonText = R.string.pine_image_permission_denied_button.stringResource(),
                onClick = { permissionController.requestPermissions() }
            )
        }

        PermissionState.DeniedPermanently -> {
            PermissionExplanationCard(
                title = R.string.pine_image_permission_denied_permanently_title.stringResource(),
                description = R.string.pine_image_permission_denied_permanently_subtitle.stringResource(),
                buttonText = R.string.pine_image_permission_denied_permanently_button.stringResource(),
                onClick = { permissionController.requestPermissions() }
            )
        }

        PermissionState.Unknown -> PineLoading()
    }
}

@Composable
fun PermissionExplanationCard(
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = onClick) {
                    Text(buttonText)
                }
            }
        }
    }
}

@Composable
fun ImagePickupScaffold(
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