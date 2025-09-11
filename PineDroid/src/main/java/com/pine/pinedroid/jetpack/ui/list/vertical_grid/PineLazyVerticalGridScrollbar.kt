package com.pine.pinedroid.jetpack.ui.list.vertical_grid


import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pine.pinedroid.utils.log.logw
import com.pine.pinedroid.utils.ui.dp2Px
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun PineLazyVerticalGridScrollbar(
    state: LazyGridState,
    elementRowCount: Int,
    modifier: Modifier = Modifier,
    draggable: Boolean = true,
    width: Dp = 24.dp,
    autoHide: Boolean = true,
    hideDelay: Long = 1500L // 1.5秒后隐藏
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isScrolling by remember { mutableStateOf(false) }
    var shouldShow by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val layoutInfo = state.layoutInfo
    val totalItems = layoutInfo.totalItemsCount
    val visibleItems = layoutInfo.visibleItemsInfo

    // 监听滚动状态变化
    val isScrollingState by remember {
        derivedStateOf {
            state.isScrollInProgress
        }
    }

    // 当滚动状态改变时，更新isScrolling状态
    LaunchedEffect(isScrollingState) {
        isScrolling = isScrollingState
        if (isScrollingState) {
            shouldShow = true
        }
    }

    // 自动隐藏逻辑
    LaunchedEffect(isScrolling, isDragging) {
        if (autoHide && !isScrolling && !isDragging) {
            delay(hideDelay)
            if (!isScrolling && !isDragging) {
                shouldShow = false
            }
        } else if (isScrolling || isDragging) {
            shouldShow = true
        }
    }

    // 透明度动画
    val alpha by animateFloatAsState(
        targetValue = if (shouldShow || !autoHide) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "scrollbar_alpha"
    )

    if (totalItems == 0 || visibleItems.isEmpty()) return

    val firstVisibleItemIndex = visibleItems.firstOrNull()?.index ?: 0
    val visibleItemsCount = visibleItems.size

    // 可滚动的item数量 = 总数量 - 一屏显示的数量
    val scrollableItems = (totalItems - visibleItemsCount).coerceAtLeast(0)

    // 滚动进度 = 当前第一个可见item索引 / 可滚动item数量
    val scrollProgress = if (scrollableItems > 0) {
        firstVisibleItemIndex.toFloat() / scrollableItems
    } else 0f

    BoxWithConstraints(
        modifier = modifier.alpha(alpha)
    ) {
        val scrollBarHeightPercent = (0.1f + 0.9f / (scrollableItems / 10f + 1f)).coerceIn(0f, 1f) //0.15f

        val trackHeight = maxHeight
        // 滚动条高度固定为15%
        val thumbHeight = trackHeight * scrollBarHeightPercent
        // 可滚动空间为85%
        val scrollableSpace = trackHeight * (1 - scrollBarHeightPercent)

        val thumbOffset = if (isDragging) {
            (dragOffset * scrollableSpace.value).dp.coerceIn(0.dp, scrollableSpace)
        } else {
            (scrollProgress * scrollableSpace.value).dp
        }

        // Track background
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(width)
                .background(
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(width / 2)
                )
        )

        // Scrollbar thumb
        Box(
            modifier = Modifier
                .offset(y = thumbOffset)
                .width(width)
                .height(thumbHeight)
                .background(
                    color = if (isDragging) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                    },
                    shape = RoundedCornerShape(width / 2)
                )
                .then(
                    if (draggable) {
                        Modifier
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = {
                                        isDragging = true
                                        shouldShow = true
                                        // 初始化拖拽偏移为当前滚动位置
                                        dragOffset = scrollProgress
                                    },
                                    onDragEnd = {
                                        isDragging = false
                                    }
                                ) { change, dragAmount ->
                                    change.consume()

                                    dragOffset = (dragOffset + dragAmount.y / trackHeight.dp2Px())
                                        .coerceIn(0f, 1f)

                                    val targetIndex = (dragOffset * (totalItems - visibleItemsCount))
                                        .toInt()
                                        .coerceIn(0, totalItems - 1)

                                    coroutineScope.launch {
                                        state.scrollBy(dragAmount.y)
                                    }
                                }
                            }
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    shouldShow = true
                                    val trackPosition = offset.y / scrollableSpace.value.dp2Px()
                                    val targetIndex = if (scrollableItems > 0) {
                                        (trackPosition * scrollableItems).toInt().coerceIn(0, totalItems - 1)
                                    } else 0

                                    coroutineScope.launch {
                                        state.animateScrollToItem(targetIndex)
                                    }
                                }
                            }
                    } else Modifier
                )
                .animateContentSize()
                .scale(
                    scaleX = if (isDragging) 1.2f else 1f,
                    scaleY = 1f
                )
        )
    }
}