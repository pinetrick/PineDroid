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
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
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
    val itemHeights = remember { mutableMapOf<Int, Dp>() }

    Column(modifier = modifier) {
        items.forEachIndexed { index, item ->
            val isDragged = draggedIndex == index

            DraggableItem(
                index = index,
                total = items.size,
                isDraggable = onItemDragged != null,
                isDragged = isDragged,
                dragOffset = if (isDragged) dragOffset else 0f,
                itemHeights = itemHeights,
                onHeightMeasured = { height -> itemHeights[index] = height },
                onDragStart = {
                    draggedIndex = index
                    dragOffset = 0f
                },
                onDrag = { offset ->
                    dragOffset += offset // 累加偏移量而不是直接设置
                },
                onDragEnd = {
                    draggedIndex = -1
                    dragOffset = 0f
                },
                onPositionChange = { from, to ->
                    onItemDragged?.invoke(from, to)
                    draggedIndex = to
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
                                .padding(end = 16.dp) // 添加右边距
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
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()

                        // 更新拖拽偏移量，让item跟随手指移动
                        onDrag(dragAmount.y)

                        // 计算位置交换 - 修复交换逻辑
                        val currentOffsetPx = dragAmount.y

                        // 计算应该移动的方向和距离
                        val direction = if (currentOffsetPx > 0) 1 else -1
                        val absOffset = Math.abs(currentOffsetPx)

                        // 获取当前item和相邻item的高度
                        val currentHeight = itemHeights[index] ?: return@detectDragGesturesAfterLongPress
                        val currentHeightPx = with(density) { currentHeight.toPx() }

                        // 计算需要移动的阈值（当前item高度的一半）
                        val threshold = currentHeightPx * 0.5f

                        if (absOffset > threshold) {
                            val newIndex = (index + direction).coerceIn(0, total - 1)
                            if (newIndex != index) {
                                onPositionChange(index, newIndex)
                                // 重置偏移量，因为位置已经交换
                                onDrag(-currentOffsetPx)
                            }
                        }
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() }
                )
            }
    ) {
        content()
    }
}

// Preview
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
                text = "可拖拽排序列表",
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

// 简单项目的Preview
@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DraggableSortListSimplePreview() {
    MaterialTheme {
        val sampleItems = listOf("苹果", "香蕉", "橙子", "葡萄", "西瓜")

        DraggableSortList(
            items = sampleItems,
            onItemDragged = { from, to ->
                println("项目从 $from 移动到 $to")
            },
            modifier = Modifier.padding(16.dp)
        ) { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}