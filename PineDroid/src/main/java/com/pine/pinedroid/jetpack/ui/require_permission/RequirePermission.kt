package com.pine.pinedroid.jetpack.ui.require_permission

import android.Manifest
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
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
                logw("RequirePermission", permissionsState.revokedPermissions.map { it.permission })
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
@Composable
fun MyScreen() {
    var hasPermission: Boolean? by remember { mutableStateOf(false) }
    val permissionController = rememberPermissionController(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ),
        onGranted = {
            hasPermission = true
            //viewModel.onPermission(true)
        },
        onDenied = {
            hasPermission = false
        },
        onShowRationale = {
            hasPermission = null
        },
    )


    if (hasPermission == true) {
        Text("有权限")
    } else {
        Button(onClick = { permissionController.requestPermissions() }) {
            Text("请求权限")
        }
    }
}