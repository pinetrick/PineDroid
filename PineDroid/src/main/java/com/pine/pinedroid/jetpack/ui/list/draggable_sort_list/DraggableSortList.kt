package com.pine.pinedroid.jetpack.ui.list.draggable_sort_list

import android.content.res.Configuration
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.pine.pinedroid.utils.log.logd
import kotlin.math.roundToInt

data class DraggableSortListBean<T>(
    var itemT: T,
    var heightDp: Dp,
    var heightFloat: Float,
    var isDragging: Boolean = false,
    var accumulatorHeightFromChoiceItem: Float = 0f,
)

@Composable
fun <T> PineDraggableSortList(
    enabledDrag: Boolean = true,
    modifier: Modifier = Modifier,
    items: List<T>,
    onItemDragged: ((from: Int, to: Int) -> Unit)? = null,
    content: @Composable (T) -> Unit
) {


    var afterReorderItems by remember {
        mutableStateOf(emptyList<DraggableSortListBean<T>>())
    }

    LaunchedEffect(items) {
        afterReorderItems = items.map {
            DraggableSortListBean(it, Dp(0f), 0f)
        }
    }
    var dragItemIndex by remember { mutableIntStateOf(0) }




    Column(modifier = modifier) {
        val density = LocalDensity.current
        var accumulatedOffset by remember { mutableFloatStateOf(0f) }

        afterReorderItems.forEachIndexed { index, item ->

            Box(
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        // 测量实际高度
                        val heightInPx = layoutCoordinates.size.height.toFloat()
                        val heightInDp = with(density) { heightInPx.toDp() }
                        item.heightDp = heightInDp
                        item.heightFloat = heightInPx
                    }
                    .offset {
                        when {
                            dragItemIndex == -1 -> IntOffset(0, 0)
                            //向上移动 accumulatedOffset = -2
                            index < dragItemIndex && accumulatedOffset < 0 && -accumulatedOffset > item.accumulatorHeightFromChoiceItem -> {
                                IntOffset(
                                    0,
                                    afterReorderItems[dragItemIndex].heightFloat.toInt()
                                )
                            }

                            //向下移动 accumulatedOffset = 2
                            index > dragItemIndex && accumulatedOffset > 0 && accumulatedOffset > item.accumulatorHeightFromChoiceItem -> {
                                IntOffset(
                                    0,
                                    -afterReorderItems[dragItemIndex].heightFloat.toInt()
                                )
                            }

                            index == dragItemIndex ->
                                IntOffset(0, accumulatedOffset.roundToInt())

                            else -> IntOffset(0, 0)
                        }
                    }
                    .pointerInput(onItemDragged != null, index, items.size) {
                        if (onItemDragged == null) return@pointerInput

                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                if (!enabledDrag) return@detectDragGesturesAfterLongPress

                                item.isDragging = true
                                dragItemIndex = index
                                accumulatedOffset = 0f

                                var totalHeight = item.heightFloat * 0.6f
                                (index - 1 downTo 0).forEach {
                                    afterReorderItems[it].accumulatorHeightFromChoiceItem =
                                        totalHeight
                                    totalHeight += afterReorderItems[it].heightFloat
                                }

                                totalHeight = item.heightFloat * 0.6f
                                (index + 1..<afterReorderItems.size).forEach {
                                    afterReorderItems[it].accumulatorHeightFromChoiceItem =
                                        totalHeight
                                    totalHeight += afterReorderItems[it].heightFloat
                                }

                            },
                            onDrag = { change, dragAmount ->
                                if (!enabledDrag) return@detectDragGesturesAfterLongPress

                                change.consume()
                                // 更新拖拽偏移量
                                accumulatedOffset += dragAmount.y
                            },
                            onDragEnd = {
                                if (!enabledDrag) return@detectDragGesturesAfterLongPress

                                item.isDragging = false
                                var destLocation = dragItemIndex
                                if (accumulatedOffset < 0) {
                                    (index - 1 downTo 0).forEach {
                                        if (afterReorderItems[it].accumulatorHeightFromChoiceItem < -accumulatedOffset)
                                            destLocation = it
                                    }
                                }

                                if (accumulatedOffset > 0) {
                                    (index + 1..<afterReorderItems.size).forEach {
                                        if (afterReorderItems[it].accumulatorHeightFromChoiceItem < accumulatedOffset)
                                            destLocation = it
                                    }
                                }



                                onItemDragged(dragItemIndex, destLocation)
                                logd("$dragItemIndex, $destLocation")
                                dragItemIndex = -1

                            }

                        )
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (item.isDragging) 0.5f else 1f)
                ) {
                    content(item.itemT)
                }
            }



            if (index < items.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
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
fun PineDraggableSortListPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "可拖拽排序列表（结束时回调）",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val sampleItems = listOf("项目 1", "项目 2", "项目 3", "项目 4", "项目 5")

            PineDraggableSortList(
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