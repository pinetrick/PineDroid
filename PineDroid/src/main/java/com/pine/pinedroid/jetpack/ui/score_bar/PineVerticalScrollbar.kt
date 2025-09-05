package com.pine.pinedroid.jetpack.ui.score_bar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.min


@Composable
fun Modifier.PineScrollIndicator(
    state: LazyGridState,
    width: Dp = 8.dp,
    enableDrag: Boolean = true
): Modifier {
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) 150 else 500
    val color = MaterialTheme.colorScheme.primary
    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration)
    )

    val density = LocalDensity.current
    val widthPx = with(density) { width.toPx() }
    val scope = rememberCoroutineScope()  // ✅ Add coroutine scope

    var isDragging by remember { mutableStateOf(false) }
    var dragStartY by remember { mutableStateOf(0f) }
    var dragStartIndex by remember { mutableStateOf(0) }

    return this
        .drawWithContent {
            drawContent()

            val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
            val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f || isDragging

            if (needDrawScrollbar && firstVisibleElementIndex != null) {
                val totalItems = state.layoutInfo.totalItemsCount
                val elementHeight = this.size.height / totalItems
                val minHeightPx = with(density) { 24.dp.toPx() } // 设置最小滚动条高度
                val scrollbarHeight = (state.layoutInfo.visibleItemsInfo.size * elementHeight)
                    .coerceAtLeast(minHeightPx)
                val scrollbarOffsetY = firstVisibleElementIndex * elementHeight

                drawRect(
                    color = color,
                    topLeft = Offset(this.size.width - widthPx, scrollbarOffsetY),
                    size = Size(widthPx, scrollbarHeight),
                    alpha = if (isDragging) 1f else alpha
                )
            }
        }
        .then(
            if (enableDrag) {
                Modifier.pointerInput(state, widthPx) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            if (offset.x > size.width - widthPx * 2) {
                                isDragging = true
                                dragStartY = offset.y
                                dragStartIndex = state.firstVisibleItemIndex
                            }
                        },
                        onDrag = { change, dragAmount ->
                            if (isDragging) {
                                val totalItems = state.layoutInfo.totalItemsCount
                                val visibleItems = state.layoutInfo.visibleItemsInfo.size

                                if (totalItems > 0) {
                                    val dragDeltaY = change.position.y - dragStartY
                                    val dragFraction = dragDeltaY / size.height
                                    val itemsToScroll = (dragFraction * totalItems).toInt()

                                    val newIndex = (dragStartIndex + itemsToScroll)
                                        .coerceIn(0, totalItems - visibleItems)

                                    scope.launch {
                                        state.scrollToItem(newIndex)
                                    }
                                }
                            }
                        },
                        onDragEnd = {
                            isDragging = false
                        }
                    )
                }
            } else {
                Modifier
            }
        )
}