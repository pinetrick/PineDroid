package com.pine.pinedroid.jetpack.ui.image

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize
import com.pine.pinedroid.R
import com.pine.pinedroid.activity.image_pickup.OneImage

@Composable
fun PineZoomableImage(
    image: OneImage,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(Size.Zero) }
    var imageSize by remember { mutableStateOf(Size.Zero) }

    val transformableState = rememberTransformableState { zoomChange, panChange, rotationChange ->
        val newScale = scale * zoomChange
        val minScale = 1f

        if (newScale >= minScale) {
            scale = newScale

            // 只有在缩放比例大于1时才允许平移
            if (scale > minScale) {
                // 计算最大允许的平移范围
                val maxOffsetX = calculateMaxOffsetX(containerSize, imageSize, scale)
                val maxOffsetY = calculateMaxOffsetY(containerSize, imageSize, scale)

                // 应用平移，但限制在允许范围内
                val newOffsetX = (offset.x + panChange.x).coerceIn(-maxOffsetX, maxOffsetX)
                val newOffsetY = (offset.y + panChange.y).coerceIn(-maxOffsetY, maxOffsetY)

                offset = Offset(newOffsetX, newOffsetY)
            } else {
                // 当缩放比例为1时，重置偏移量
                offset = Offset.Zero
            }
        }
    }

    // 双击重置手势
    val doubleClickState = remember { mutableStateOf(0L) }

    Box(
        modifier = modifier
            .background(Color.Black)
            .onGloballyPositioned { layoutCoordinates ->
                containerSize = layoutCoordinates.size.toSize()
            }
            .transformable(transformableState)
            .clickable {
                val now = System.currentTimeMillis()
                if (now - doubleClickState.value < 300) {
                    // 双击重置
                    scale = 1f
                    offset = Offset.Zero
                }
                doubleClickState.value = now
            }
    ) {
        PineAsyncImage(
            model = image,
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { layoutCoordinates ->
                    imageSize = layoutCoordinates.size.toSize()
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentScale = ContentScale.Fit
        )
    }
}

// 计算水平方向最大平移距离
private fun calculateMaxOffsetX(containerSize: Size, imageSize: Size, scale: Float): Float {
    if (containerSize.width == 0f || imageSize.width == 0f) return 0f

    val scaledImageWidth = imageSize.width * scale
    val emptySpace = (scaledImageWidth - containerSize.width) / 2f
    return emptySpace.coerceAtLeast(0f)
}

// 计算垂直方向最大平移距离
private fun calculateMaxOffsetY(containerSize: Size, imageSize: Size, scale: Float): Float {
    if (containerSize.height == 0f || imageSize.height == 0f) return 0f

    val scaledImageHeight = imageSize.height * scale
    val emptySpace = (scaledImageHeight - containerSize.height) / 2f
    return emptySpace.coerceAtLeast(0f)
}

@Preview
@Composable
fun PreviewZoomablePineImage() {
    PineZoomableImage(
        image = OneImage.Resource(R.drawable.camera),
        modifier = Modifier.fillMaxSize()
    )
}