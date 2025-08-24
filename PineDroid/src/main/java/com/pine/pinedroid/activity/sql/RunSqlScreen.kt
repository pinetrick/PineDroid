package com.pine.pinedroid.activity.sql

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.db.DbRecord
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation
import com.pine.pinedroid.ui.Colour
import com.pine.pinedroid.utils.ui.pct
import com.pine.pinedroid.utils.ui.spwh
import androidx.compose.foundation.lazy.items
import com.pine.pinedroid.db.ColumnInfo

@Composable
fun RunSqlScreen(
    navController: NavController? = null,
    dbName: String = "",
    tableName: String = "",
    viewModel: RunSqlScreenVM = viewModel()
) {
    // 处理导航
    HandleNavigation(navController = navController, viewModel = viewModel)

    LaunchedEffect(dbName, tableName) {
        if (dbName.isNotEmpty()) {
            viewModel.initialize(dbName, tableName)
        }
    }

    // 获取状态
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()


    RunSqlContent(
        state = viewState,
        onSqlChanged = { viewModel.updateSql(it) },
        onExecute = { viewModel.onRunSql() },
        onReturn = { viewModel.navigateBack() }
    )
}

@Composable
fun RunSqlContent(
    state: RunSqlScreenStatus,
    onSqlChanged: (String) -> Unit,
    onExecute: () -> Unit,
    onReturn: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(1.pct)
    ) {

        // SQL输入区域
        SqlInputSection(
            sql = state.sql,
            onSqlChanged = onSqlChanged,
            onReturn = onReturn,
            onExecute = onExecute,
            modifier = Modifier
                .fillMaxWidth().height(15.pct)
        )

        Spacer(modifier = Modifier.height(1.dp).background(Colour.gray))

        // 结果表格区域
        SqlResultTable(
            records = state.table,
            header = state.tableHeader,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
        )
    }
}

@Composable
fun SqlInputSection(
    sql: String,
    onSqlChanged: (String) -> Unit,
    onReturn: () -> Unit,
    onExecute: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) {
            // 执行按钮 - 播放图标版本
            IconButton(
                onClick = onReturn,
                modifier = Modifier
                    .fillMaxHeight()
                    .size(8.pct)
                    .background(
                        MaterialTheme.colorScheme.primary
                    )
            ) {
                Icon(
                    imageVector =Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "执行SQL",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 使用BasicTextField替代TextField来完全控制padding
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(1.pct)
                    )
                    .padding(1.dp) // 仅保留外部边框的padding
            ) {
                BasicTextField(
                    value = sql,
                    onValueChange = onSqlChanged,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 4.dp), // 最小化的内部padding
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 12.sp,
                        lineHeight = 14.sp // 调整行高来减少垂直间距
                    ),
                    decorationBox = { innerTextField ->
                        if (sql.isEmpty()) {
                            Text(
                                "输入SQL语句，例如: SELECT * FROM table_name",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        innerTextField()
                    },
                    maxLines = 3
                )
            }

            // 执行按钮 - 播放图标版本
            IconButton(
                onClick = onExecute,
                modifier = Modifier
                    .fillMaxHeight()
                    .size(8.pct)
                    .background(
                        MaterialTheme.colorScheme.primary
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "执行SQL",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun SqlResultTable(
    records: List<DbRecord>,
    header: List<ColumnInfo>,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }

    Column(modifier = modifier) {
        Text(
            text = "执行结果 (${records.size} 条记录)",
            fontSize = 10.spwh,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
        )

        if (records.isEmpty()) {
            // 空状态
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "暂无数据或未执行查询",
                    fontSize = 12.spwh,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // 缩放控制
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { scale = (scale - 0.1f).coerceAtLeast(0.5f) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.ZoomOut,
                        contentDescription = "缩小"
                    )
                }

                Text(
                    text = "${(scale * 100).toInt()}%",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                IconButton(
                    onClick = { scale = (scale + 0.1f).coerceAtMost(2f) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.ZoomIn,
                        contentDescription = "放大"
                    )
                }
            }

            DataTable(
                header = header,
                records = records,
                scale = scale,
                modifier = Modifier.fillMaxSize()
            )

//            // 表格内容 - REMOVE verticalScroll from here
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .border(1.dp, MaterialTheme.colorScheme.outline)
//                    .horizontalScroll(rememberScrollState()) // Keep only horizontal scroll
//            ) {
//                LazyColumn(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .graphicsLayer(scaleX = scale, scaleY = scale)
//                ) {
//
//                    // 表头
//                    item {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .background(MaterialTheme.colorScheme.primaryContainer)
//                        ) {
//                            header.forEach { columnInfo ->
//                                Text(
//                                    text = columnInfo.name,
//                                    fontSize = 8.spwh,
//                                    fontWeight = FontWeight.Bold,
//                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
//                                    modifier = Modifier
//                                        .padding(1.pct)
//                                )
//                            }
//                        }
//                    }
//
//                    // 数据行（推荐 items(list)）
//                    items(records) { record ->
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
//                        ) {
//                            record.kvs.values.forEach { value ->
//                                Text(
//                                    text = value?.toString() ?: "NULL",
//                                    fontSize = 8.spwh,
//                                    modifier = Modifier
//                                        .padding(1.pct)
//                                )
//                            }
//                        }
//                    }
//                }
//
//            }
        }
    }
}


@Composable
fun DataTable(
    header: List<ColumnInfo>, // 假设 ColumnInfo 有 name 属性
    records: List<DbRecord>,    // 假设 Record 有 kvs 属性
    scale: Float = 1f,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .horizontalScroll(rememberScrollState())
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(scaleX = scale, scaleY = scale)
        ) {
            // 表头
            item {
                TableHeaderRow(header)
            }

            // 数据行
            items(records) { record ->
                TableDataRow(record)
            }
        }
    }
}

@Composable
private fun TableHeaderRow(header: List<ColumnInfo>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        header.forEach { columnInfo ->
            Text(
                text = columnInfo.name,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .weight(1f) // 使用 weight 确保均匀分布
                    .padding(4.dp) // 使用 dp 而不是 pct
            )
        }
    }
}

@Composable
private fun TableDataRow(record: DbRecord) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        record.kvs.values.forEach { value ->
            Text(
                text = value?.toString() ?: "NULL",
                fontSize = 8.sp,
                modifier = Modifier
                    .weight(1f) // 使用 weight 确保均匀分布
                    .padding(4.dp) // 使用 dp 而不是 pct
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun RunSqlScreenPreview() {
    RunSqlScreen(null)
}
