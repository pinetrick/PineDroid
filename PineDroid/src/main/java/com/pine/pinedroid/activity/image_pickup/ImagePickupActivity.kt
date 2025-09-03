package com.pine.pinedroid.activity.image_pickup

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.pine.pinedroid.jetpack.ui.require_permission.rememberPermissionController

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
                // 构建正确的权限列表
                val requiredPermissions = buildList {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                        // Android 13+ 用新的媒体权限
//                        add(Manifest.permission.READ_MEDIA_IMAGES)
//                        // 如果还需要视频/音频，加上：
//                         add(Manifest.permission.READ_MEDIA_VIDEO)
//                         add(Manifest.permission.READ_MEDIA_AUDIO)
                    } else {
                        // Android 12 及以下，还是用旧的存储权限
                        add(Manifest.permission.READ_EXTERNAL_STORAGE)
                        add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    }

                    // 相机权限（如果需要）
                    if (allowCamera) {
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
                        ImagePickupScreen(
                            allowCamera = allowCamera,
                            allowMultiple = allowMultiple,
                            onBack = { finish() },
                            onComplete = { selectedUris ->
                                ImagePickup.result.clear()
                                ImagePickup.result.addAll(selectedUris)
                                onResultCallback?.invoke(selectedUris)
                                finish()
                            }
                        )
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
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onResultCallback = null
    }
}
