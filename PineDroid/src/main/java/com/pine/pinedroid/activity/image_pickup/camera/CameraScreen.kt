package com.pine.pinedroid.activity.image_pickup.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.jetpack.ui.nav.GeneralPineScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun CameraScreen(
    navController: NavHostController? = null,
    viewModel: CameraScreenVM = viewModel()
) {
    HandleNavigation(navController = navController, viewModel = viewModel)

    val viewState by viewModel.viewState.collectAsState()


    // 模拟加载图片数据（实际应用中应该从媒体库加载）
    LaunchedEffect(Unit) {
        viewModel.onInit()
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 相机预览区域 - 这里应该是实际的相机预览
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // 这里应该显示相机预览，暂时用占位文本
            Text(
                text = "相机预览",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        // 顶部操作栏
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 可以添加一些顶部指示器，如模式选择等
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
                        imageVector = if (viewState.flashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "闪光灯",
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
                        ),
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
                        contentDescription = "切换摄像头",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun Preview(){
    CameraScreen()
}