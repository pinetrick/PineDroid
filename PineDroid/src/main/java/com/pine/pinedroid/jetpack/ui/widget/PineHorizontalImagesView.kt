package com.pine.pinedroid.jetpack.ui.widget

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pine.pinedroid.activity.image_pickup.DEMO_ONE_IMAGE_LIST
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.jetpack.ui.image.PineAsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PineHorizontalImageView(
    images: List<OneImage>,
    modifier: Modifier = Modifier,
    onClick: (OneImage, Int) -> Unit = { _, _ -> },
    onPageChange: (Int) -> Unit = {}
) {
    require(images.isNotEmpty()) { "Images list cannot be empty" }

    val pagerState = rememberPagerState(
        pageCount = { images.size },
        initialPage = 0
    )

    // 修复：使用 remember 包装 derivedStateOf
    val currentPage by remember(pagerState) {
        derivedStateOf { pagerState.currentPage }
    }

    // 监听页面变化
    LaunchedEffect(currentPage) {
        onPageChange(currentPage)
    }

    Box(
        modifier = modifier
            .height(300.dp)
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val image = images[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onClick(image, page) }
            ) {
                PineAsyncImage(
                    model = image,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // 顶部操作栏
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 图片数量指示器
            if (images.size > 1) {
                Text(
                    text = "${currentPage + 1}/${images.size}",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // 底部指示器
        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                repeat(images.size) { index ->
                    val isSelected = index == currentPage
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)
                            )
                    )
                    if (index < images.size - 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
        }


    }
}

@Preview
@Composable
fun PineHorizontalImageViewPreview() {
    val sampleImages = DEMO_ONE_IMAGE_LIST

    PineHorizontalImageView(
        images = sampleImages,
        onClick = { image, index ->
            // 处理点击逻辑
        }
    )
}