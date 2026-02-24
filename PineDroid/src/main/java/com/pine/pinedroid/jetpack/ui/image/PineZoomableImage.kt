package com.pine.pinedroid.jetpack.ui.image

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.pine.pinedroid.R
import com.pine.pinedroid.activity.image_pickup.OneImage

@Composable
fun PineZoomableImage(
    image: OneImage,
    modifier: Modifier = Modifier,
    onScaleChanged: ((Float) -> Unit)? = null
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    fun clampOffset(raw: Offset, currentScale: Float): Offset {
        val maxX = (containerSize.width * (currentScale - 1) / 2f).coerceAtLeast(0f)
        val maxY = (containerSize.height * (currentScale - 1) / 2f).coerceAtLeast(0f)
        return Offset(
            raw.x.coerceIn(-maxX, maxX),
            raw.y.coerceIn(-maxY, maxY)
        )
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .clipToBounds()
            .onSizeChanged { containerSize = it }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (scale > 1f) {
                            scale = 1f
                            offset = Offset.Zero
                            onScaleChanged?.invoke(1f)
                        } else {
                            scale = 3f
                            onScaleChanged?.invoke(3f)
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                // 自定义手势处理：
                // - 多指（捏合）：立即消费，处理缩放（不等 touchSlop）
                // - 单指 + 已缩放：消费，处理平移
                // - 单指 + 未缩放：不消费，让父级 HorizontalPager 处理左右滑动
                // 注意：key 用 Unit 而非 scale，避免 scale 变化时中断正在进行的手势
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    var pastTouchSlop = false
                    var totalPan = Offset.Zero
                    val touchSlop = viewConfiguration.touchSlop

                    do {
                        val event = awaitPointerEvent()
                        val canceled = event.changes.any { it.isConsumed }
                        if (!canceled) {
                            val isMultiTouch = event.changes.size > 1
                            val zoomChange = event.calculateZoom()
                            val panChange = event.calculatePan()

                            if (!pastTouchSlop) {
                                if (isMultiTouch) {
                                    // 多指立即开始，不等 slop
                                    pastTouchSlop = true
                                } else {
                                    totalPan += panChange
                                    if (totalPan.getDistance() > touchSlop) {
                                        pastTouchSlop = true
                                    }
                                }
                            }

                            if (pastTouchSlop) {
                                if (isMultiTouch || scale > 1f) {
                                    val newScale = (scale * zoomChange).coerceIn(1f, 5f)
                                    scale = newScale
                                    onScaleChanged?.invoke(newScale)
                                    if (newScale > 1f) {
                                        offset = clampOffset(offset + panChange, newScale)
                                    } else {
                                        offset = Offset.Zero
                                    }
                                    event.changes.forEach { it.consume() }
                                }
                            }
                        }
                    } while (!canceled && event.changes.any { it.pressed })
                }
            }
    ) {
        PineAsyncImage(
            model = image,
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize()
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


@Preview
@Composable
fun PreviewZoomablePineImage() {
    PineZoomableImage(
        image = OneImage.Resource(R.drawable.camera),
        modifier = Modifier.fillMaxSize()
    )
}
