package com.pine.pinedroid.jetpack.ui.image

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import com.pine.pinedroid.activity.image_pickup.OneImage

@Composable
fun ZoomablePineImage(
    image: OneImage,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val transformableState = rememberTransformableState { zoomChange, panChange, rotationChange ->
        // 计算新的缩放比例，但不允许小于1（即不允许缩小到小于控件尺寸）
        val newScale = scale * zoomChange
        scale = newScale.coerceAtLeast(1f)

        // 只有在缩放比例大于1时才允许平移
        if (scale > 1f) {
            offsetX += panChange.x
            offsetY += panChange.y
        } else {
            // 当缩放比例为1时，重置偏移量
            offsetX = 0f
            offsetY = 0f
        }
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .transformable(transformableState)
    ) {
        PineAsyncImage(
            model = image,
            contentDescription = "预览图片",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                ),
            contentScale = ContentScale.Fit
        )
    }
}