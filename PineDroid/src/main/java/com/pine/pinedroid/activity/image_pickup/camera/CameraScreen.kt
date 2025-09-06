package com.pine.pinedroid.activity.image_pickup.camera

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pine.pinedroid.R
import com.pine.pinedroid.jetpack.ui.CameraPreview
import com.pine.pinedroid.jetpack.ui.font.PineIcon
import com.pine.pinedroid.jetpack.ui.image.ZoomablePineImage
import com.pine.pinedroid.jetpack.ui.loading.PineLoading
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation
import com.pine.pinedroid.utils.ui.pct
import com.pine.pinedroid.utils.ui.spwh

@Composable
fun CameraScreen(
    navController: NavHostController? = null,
    cameraLauncher: ActivityResultLauncher<Uri>? = null,
    viewModel: CameraScreenVM = viewModel()
) {


    val viewState by viewModel.viewState.collectAsState()
    HandleNavigation(navController = navController, viewModel = viewModel) {
        viewModel.onInit(cameraLauncher)
    }



    if (viewState.handlingPicture) {
        PineLoading(
            title = stringResource(R.string.pine_camera_processing),
            subtitle = stringResource(R.string.pine_camera_starting_subtitle)
        )
    } else if (viewState.useSystemCamera) {
        PineLoading(
            title = stringResource(R.string.pine_camera_starting),
            subtitle = stringResource(R.string.pine_camera_starting_subtitle)
        )
    } else {
        JetPackCameraView(viewModel, viewState)
    }


}

@Composable
fun CameraStarting() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 加载指示器
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = Color.White,
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 提示文本
            Text(
                text = stringResource(R.string.pine_camera_starting),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.pine_camera_starting_subtitle),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun JetPackCameraView(viewModel: CameraScreenVM, viewState: CameraScreenState) {
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 相机预览区域 - 这里应该是实际的相机预览
        // 真正的相机预览
        if (viewState.cameraPhoto == null) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                cameraSelector = if (viewState.isFrontCamera) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                },
                flashMode = viewState.flashMode,
                onUseCase = { imageCapture = it }
            )
        } else {
            ZoomablePineImage(
                image = viewState.cameraPhoto,
            )
        }

        // 顶部操作栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(2.pct),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (viewState.cameraPhoto != null) {
                IconButton(onClick = { viewModel.retry() }) {
                    PineIcon(
                        text = "\uf00d",
                        fontSize = 24.spwh,
                        color = Color.White,
                    )
                }

                IconButton(onClick = { viewModel.confirmPicture() }) {
                    PineIcon(
                        text = "\uf00c",
                        fontSize = 24.spwh,
                        color = Color.White,
                    )
                }
            } else {
                IconButton(onClick = { viewModel.navigateBack() }) {
                    PineIcon(
                        text = "\uf060",
                        fontSize = 24.spwh,
                        color = Color.White,
                    )
                }
            }
        }

        // 底部操作栏
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 拍照按钮和相册入口
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 闪光灯按钮
                IconButton(
                    onClick = { viewModel.toggleFlash() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = when (viewState.flashMode) {
                            FLASH_MODE_ON -> Icons.Default.FlashOn
                            FLASH_MODE_OFF -> Icons.Default.FlashOff
                            else -> Icons.Default.FlashAuto
                        },
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // 拍照按钮
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        )
                        .clickable { viewModel.takePicture(imageCapture) },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                color = Color.White,
                                shape = CircleShape
                            )
                            .padding(4.dp)
                            .background(
                                color = Color.Red,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {}
                }

                // 切换摄像头按钮
                IconButton(
                    onClick = { viewModel.switchCamera() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Preview()
@Composable
fun Preview() {
    CameraScreen()
}