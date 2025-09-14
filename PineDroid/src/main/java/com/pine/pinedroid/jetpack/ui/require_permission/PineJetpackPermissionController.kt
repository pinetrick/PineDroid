package com.pine.pinedroid.jetpack.ui.require_permission

import android.Manifest
import android.os.Build
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.pine.pinedroid.utils.log.logw

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberPermissionController(
    permissions: List<String>,
    onGranted: suspend () -> Unit = {},
    onDenied: suspend (controller: PermissionController) -> Unit = {},
    onShowRationale: () -> Unit = {},
): PermissionController {
    val permissionsState = rememberMultiplePermissionsState(permissions = permissions)

    val controller = remember {
        PermissionController(
            requestPermissions = {
                permissionsState.launchMultiplePermissionRequest()
            },
            hasPermission = {
                permissionsState.allPermissionsGranted
            }
        )
    }

    // 处理权限状态变化
    LaunchedEffect(permissionsState.allPermissionsGranted, permissionsState.shouldShowRationale) {
        when {
            permissionsState.allPermissionsGranted -> {
                onGranted()
            }

            permissionsState.shouldShowRationale -> {
                onShowRationale()
            }

            else -> {
                logw("RequiredPermission", permissionsState.permissions.map { it.permission })
                logw("DeniedPermission", permissionsState.revokedPermissions.map { it.permission })
                onDenied(controller)
            }
        }
    }

    return controller
}

class PermissionController(
    val requestPermissions: () -> Unit,
    val hasPermission: () -> Boolean
)

// 使用方式
@Preview
@Composable
fun MyScreen() {
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


        add(Manifest.permission.CAMERA)
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