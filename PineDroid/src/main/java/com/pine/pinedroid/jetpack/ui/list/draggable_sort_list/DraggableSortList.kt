package com.pine.pinedroid.jetpack.ui.list.draggable_sort_list

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T> DraggableSortList(
    modifier: Modifier = Modifier,
    items: List<T>,
    onItemDragged: ((from: Int, to: Int) -> Unit)? = null,
    content: @Composable (T) -> Unit
) {
    var draggedIndex by remember { mutableIntStateOf(-1) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var targetIndex by remember { mutableIntStateOf(-1) } // 目标位置
    val itemHeights = remember { mutableMapOf<Int, Dp>() }
    var pendingMove: Pair<Int, Int>? by remember { mutableStateOf(null) } // 待处理的移动

    Column(modifier = modifier) {
        items.forEachIndexed { index, item ->
            val isDragged = draggedIndex == index
            val isTargetPosition = targetIndex == index && !isDragged

            DraggableItem(
                index = index,
                total = items.size,
                isDraggable = onItemDragged != null,
                isDragged = isDragged,
                isTargetPosition = isTargetPosition,
                dragOffset = if (isDragged) dragOffset else 0f,
                itemHeights = itemHeights,
                onHeightMeasured = { height -> itemHeights[index] = height },
                onDragStart = {
                    draggedIndex = index
                    targetIndex = -1
                    dragOffset = 0f
                    pendingMove = null
                },
                onDrag = { offset ->
                    dragOffset = offset
                },
                onDragEnd = {
                    // 拖拽结束时执行回调
                    pendingMove?.let { (from, to) ->
                        onItemDragged?.invoke(from, to)
                    }
                    draggedIndex = -1
                    targetIndex = -1
                    dragOffset = 0f
                    pendingMove = null
                },
                onPositionChange = { from, to ->
                    // 只在内部记录位置变化，不立即回调
                    targetIndex = to
                    pendingMove = from to to
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (isDragged) 0.5f else 1f)
                ) {
                    content(item)

                    // 拖拽手柄 - 添加右边距
                    if (onItemDragged != null) {
                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription = "拖拽排序",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                                .size(24.dp)
                        )
                    }
                }
            }

            if (index < items.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DraggableItem(
    index: Int,
    total: Int,
    isDraggable: Boolean,
    isDragged: Boolean,
    isTargetPosition: Boolean,
    dragOffset: Float,
    itemHeights: Map<Int, Dp>,
    onHeightMeasured: (Dp) -> Unit,
    onDragStart: (Int) -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onPositionChange: (from: Int, to: Int) -> Unit,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    var accumulatedOffset by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .onGloballyPositioned { layoutCoordinates ->
                // 测量实际高度
                val heightInPx = layoutCoordinates.size.height.toFloat()
                val heightInDp = with(density) { heightInPx.toDp() }
                onHeightMeasured(heightInDp)
            }
            .offset(y = with(density) { dragOffset.toDp() })
            .pointerInput(isDraggable, itemHeights, index, total) {
                if (!isDraggable) return@pointerInput

                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        onDragStart(index)
                        accumulatedOffset = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()

                        // 更新拖拽偏移量
                        accumulatedOffset += dragAmount.y
                        onDrag(accumulatedOffset)

                        // 计算位置交换
                        val currentOffsetPx = accumulatedOffset
                        val direction = if (currentOffsetPx > 0) 1 else -1
                        val absOffset = Math.abs(currentOffsetPx)

                        // 获取当前item的高度
                        val currentHeight = itemHeights[index] ?: return@detectDragGesturesAfterLongPress
                        val currentHeightPx = with(density) { currentHeight.toPx() }

                        // 计算需要移动的阈值（当前item高度的60%）
                        val threshold = currentHeightPx * 0.6f

                        if (absOffset > threshold) {
                            val newIndex = (index + direction).coerceIn(0, total - 1)
                            if (newIndex != index) {
                                onPositionChange(index, newIndex)
                                // 重置累积偏移量，因为位置已经交换
                                accumulatedOffset = 0f
                                onDrag(0f)
                            }
                        }
                    },
                    onDragEnd = {
                        onDragEnd()
                        accumulatedOffset = 0f
                    },
                    onDragCancel = {
                        onDragEnd()
                        accumulatedOffset = 0f
                    }
                )
            }
    ) {
        content()
    }
}

// 使用外部状态管理的版本 - 在拖拽过程中实时更新UI
@Composable
fun <T> DraggableSortListWithState(
    modifier: Modifier = Modifier,
    items: List<T>,
    onItemsReorder: (List<T>) -> Unit,
    content: @Composable (T) -> Unit
) {
    var draggedIndex by remember { mutableIntStateOf(-1) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var currentItems by remember { mutableStateOf(items) }
    val itemHeights = remember { mutableMapOf<Int, Dp>() }

    Column(modifier = modifier) {
        currentItems.forEachIndexed { index, item ->
            val isDragged = draggedIndex == index

            DraggableItemWithState(
                index = index,
                item = item,
                total = currentItems.size,
                isDragged = isDragged,
                dragOffset = if (isDragged) dragOffset else 0f,
                itemHeights = itemHeights,
                onHeightMeasured = { height -> itemHeights[index] = height },
                onDragStart = {
                    draggedIndex = index
                    dragOffset = 0f
                },
                onDrag = { offset ->
                    dragOffset = offset
                },
                onDragEnd = {
                    // 拖拽结束时执行回调
                    onItemsReorder(currentItems)
                    draggedIndex = -1
                    dragOffset = 0f
                },
                onPositionChange = { from, to ->
                    // 实时更新UI显示
                    val newItems = currentItems.toMutableList()
                    val movedItem = newItems.removeAt(from)
                    newItems.add(to, movedItem)
                    currentItems = newItems
                    // 更新拖拽索引到新位置
                    draggedIndex = to
                },
                content = content
            )

            if (index < currentItems.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun <T> DraggableItemWithState(
    index: Int,
    item: T,
    total: Int,
    isDragged: Boolean,
    dragOffset: Float,
    itemHeights: Map<Int, Dp>,
    onHeightMeasured: (Dp) -> Unit,
    onDragStart: (Int) -> Unit,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onPositionChange: (from: Int, to: Int) -> Unit,
    content: @Composable (T) -> Unit,
) {
    val density = LocalDensity.current
    var accumulatedOffset by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .onGloballyPositioned { layoutCoordinates ->
                // 测量实际高度
                val heightInPx = layoutCoordinates.size.height.toFloat()
                val heightInDp = with(density) { heightInPx.toDp() }
                onHeightMeasured(heightInDp)
            }
            .offset(y = with(density) { dragOffset.toDp() })
            .pointerInput(itemHeights, index, total) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        onDragStart(index)
                        accumulatedOffset = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()

                        // 更新拖拽偏移量
                        accumulatedOffset += dragAmount.y
                        onDrag(accumulatedOffset)

                        // 计算位置交换
                        val currentOffsetPx = accumulatedOffset
                        val direction = if (currentOffsetPx > 0) 1 else -1
                        val absOffset = Math.abs(currentOffsetPx)

                        // 获取当前item的高度
                        val currentHeight = itemHeights[index] ?: return@detectDragGesturesAfterLongPress
                        val currentHeightPx = with(density) { currentHeight.toPx() }

                        // 计算需要移动的阈值
                        val threshold = currentHeightPx * 0.6f

                        if (absOffset > threshold) {
                            val newIndex = (index + direction).coerceIn(0, total - 1)
                            if (newIndex != index) {
                                onPositionChange(index, newIndex)
                                // 重置累积偏移量
                                accumulatedOffset = 0f
                                onDrag(0f)
                            }
                        }
                    },
                    onDragEnd = {
                        onDragEnd()
                        accumulatedOffset = 0f
                    },
                    onDragCancel = {
                        onDragEnd()
                        accumulatedOffset = 0f
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (isDragged) 0.5f else 1f)
        ) {
            content(item)

            // 拖拽手柄
            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = "拖拽排序",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .size(24.dp)
            )
        }
    }
}

// Preview - 基础版本
@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DraggableSortListPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "可拖拽排序列表（结束时回调）",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val sampleItems = listOf("项目 1", "项目 2", "项目 3", "项目 4", "项目 5")

            DraggableSortList(
                items = sampleItems,
                onItemDragged = { from, to ->
                    println("项目从 $from 移动到 $to")
                }
            ) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

// 使用外部状态管理的Preview - 推荐使用这种方式
@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
fun DraggableSortListWithStatePreview() {
    MaterialTheme {
        var items by remember { mutableStateOf(listOf("第一项", "第二项", "第三项", "第四项", "第五项")) }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "实时更新的拖拽排序",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DraggableSortListWithState(
                items = items,
                onItemsReorder = { newItems ->
                    items = newItems
                    println("新顺序: $newItems")
                }
            ) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(text = item)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "当前顺序: ${items.joinToString()}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}