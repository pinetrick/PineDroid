@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.pine.pinedroid.activity.image_pickup.preview


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pine.pinedroid.R
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.jetpack.ui.image.PineZoomableImage
import com.pine.pinedroid.jetpack.ui.nav.PineGeneralScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ImagePreviewScreen(
    navController: NavHostController? = null,
    viewModel: ImagePreviewScreenVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    HandleNavigation(navController = navController, viewModel = viewModel) {
        viewModel.onInit()
    }

    PineGeneralScreen(
        title = {
            PineTopAppBar(
                title = stringResource(R.string.pine_preview_title) + if(viewState.images.count() > 1) "(${viewState.currentIndex + 1}/${viewState.images.size})" else "",
                onReturn = viewModel::navigateBack
            )
        },
        content = {
            ImagePreviewScreenContent(
                images = viewState.images,
                currentIndex = viewState.currentIndex,
                onIndexChanged = viewModel::onSwipeToIndex
            )
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImagePreviewScreenContent(
    images: List<OneImage>,
    currentIndex: Int = 0,
    onIndexChanged: (Int) -> Unit = {}
) {
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
        val pagerState = rememberPagerState(
            initialPage = currentIndex,
            pageCount = { images.size }
        )

        // 监听页面变化
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collectLatest { page ->
                onIndexChanged(page)
            }
        }

        // 当外部 currentIndex 变化时，同步到 pager
        LaunchedEffect(currentIndex) {
            if (currentIndex != pagerState.currentPage) {
                pagerState.animateScrollToPage(currentIndex)
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                PineZoomableImage(
                    image = images[page],
                    modifier = Modifier.fillMaxSize()
                )

                // 显示图片索引指示器（可选）
                ImageIndexIndicator(
                    currentIndex = page,
                    totalCount = images.size,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color.Black.copy(alpha = 0.5f))
                )
            }
        }
    }
}

// 图片索引指示器组件
@Composable
fun ImageIndexIndicator(
    currentIndex: Int,
    totalCount: Int,
    modifier: Modifier = Modifier
) {
    if (totalCount <= 1) return // 只有一张图片时不显示指示器

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${currentIndex + 1}/$totalCount",
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Preview
@Composable
fun Preview() {
    ImagePreviewScreen()
}