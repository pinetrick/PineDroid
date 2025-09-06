package com.pine.pinedroid.activity.image_pickup.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pine.pinedroid.R
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.activity.image_pickup.pickup.ImagePickupScreen
import com.pine.pinedroid.jetpack.ui.image.ZoomablePineImage
import com.pine.pinedroid.jetpack.ui.nav.GeneralPineScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun ImagePreviewScreen(
    navController: NavHostController? = null,
    viewModel: ImagePreviewScreenVM = viewModel()
) {
    HandleNavigation(navController = navController, viewModel = viewModel)

    val viewState by viewModel.viewState.collectAsState()


    // 模拟加载图片数据（实际应用中应该从媒体库加载）
    LaunchedEffect(Unit) {
        viewModel.runOnce {
            viewModel.onInit()
        }
    }

    GeneralPineScreen(
        title = {
            PineTopAppBar(stringResource(R.string.pine_preview_title), onReturn =  viewModel::navigateBack)
        },
        content = {
            ImagePreviewScreenContent(
                viewState.images
            )
        },
    )



}


@Composable
fun ImagePreviewScreenContent(images: List<OneImage>) {
    if (images.isEmpty()) {
        // 如果没有图片，显示空状态
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.pine_preview_no_picture))
        }
    } else {
        // 显示第一张图片并支持手势缩放
        ZoomablePineImage(
            image = images.first(),
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Preview
@Composable
fun Preview() {
    ImagePreviewScreen()
}