package com.pine.pinedroid.jetpack.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.pine.pinedroid.utils.ui.pct


@Composable
fun PineZoomableTable(
    columnCount: Int,
    lineCount: Int,
    renderCell: @Composable (rowIndex: Int, columnIndex: Int, scale: Float) -> Unit,
    header: (@Composable (columnIndex: Int, scale: Float) -> Unit)? = null,
    initialColumnWidth: Dp = 20.pct
) {
    var scale by remember { mutableFloatStateOf(1f) }
    val horizontalScrollState = rememberScrollState()

    val columnWidths = remember(columnCount) {
        mutableStateOf(List(columnCount) { initialColumnWidth })
    }

    val transformableState = rememberTransformableState { zoomChange, _, _ ->
        scale = (scale * zoomChange).coerceIn(0.3f, 5f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .transformable(state = transformableState)
    ) {
        Column(modifier = Modifier.align(Alignment.TopStart)) {
            // 表头 — 与数据行共享同一水平滚动状态
            header?.let { headerComposable ->
                Box(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                    Row {
                        repeat(columnCount) { columnIndex ->
                            Box(
                                modifier = Modifier.width(
                                    columnWidths.value.getOrElse(columnIndex) { initialColumnWidth } * scale
                                )
                            ) {
                                headerComposable(columnIndex, scale)
                            }
                        }
                    }
                }
            }

            // 数据行 — LazyColumn 按需渲染，每行独立共享水平滚动状态
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(lineCount) { rowIndex ->
                    Box(modifier = Modifier.horizontalScroll(horizontalScrollState)) {
                        Row {
                            repeat(columnCount) { columnIndex ->
                                Box(
                                    modifier = Modifier.width(
                                        columnWidths.value.getOrElse(columnIndex) { initialColumnWidth } * scale
                                    )
                                ) {
                                    renderCell(rowIndex, columnIndex, scale)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ZoomableTablePreview() {
    PineZoomableTable(
        columnCount = 3,
        lineCount = 3,
        renderCell = { rowIndex, columnIndex, _ ->
            Text("Hello$rowIndex,$columnIndex")
        },
        header = { columnIndex, _ ->
            Text("Header$columnIndex")
        }
    )
}
