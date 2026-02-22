package com.pine.pinedroid.activity.sql

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.db.ColumnInfo
import com.pine.pinedroid.db.DbRecord
import com.pine.pinedroid.db.bean.fakeColumnInfos
import com.pine.pinedroid.db.bean.fakeDbRecords
import com.pine.pinedroid.jetpack.ui.PineZoomableTable
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation
import com.pine.pinedroid.ui.Colour
import com.pine.pinedroid.utils.ui.pct
import com.pine.pinedroid.utils.ui.spwh

@Composable
fun RunSqlScreen(
    navController: NavController? = null,
    dbName: String = "",
    tableName: String = "",
    viewModel: RunSqlScreenVM = viewModel()
) {
    HandleNavigation(navController = navController, viewModel = viewModel)

    LaunchedEffect(dbName, tableName) {
        if (dbName.isNotEmpty()) {
            viewModel.initialize(dbName, tableName)
        }
    }

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    RunSqlContent(
        state = viewState,
        onSqlChanged = { viewModel.updateSql(it) },
        onExecute = { if (!viewState.isLoading) viewModel.onRunSql() },
        onReturn = { viewModel.navigateBack() },
        onCellClick = { record, columnName, value ->
            viewModel.startEdit(record, columnName, value)
        },
    )

    // 编辑弹框
    viewState.editingCell?.let { editing ->
        EditCellDialog(
            columnName = editing.columnName,
            initialValue = editing.currentValue,
            onConfirm = { viewModel.confirmEdit(it) },
            onDismiss = { viewModel.cancelEdit() },
        )
    }
}

@Composable
fun RunSqlContent(
    state: RunSqlScreenStatus,
    onSqlChanged: (String) -> Unit,
    onExecute: () -> Unit,
    onReturn: () -> Unit,
    onCellClick: (record: DbRecord, columnName: String, currentValue: String) -> Unit = { _, _, _ -> },
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(1.pct)
    ) {
        SqlInputSection(
            sql = state.sql,
            onSqlChanged = onSqlChanged,
            onReturn = onReturn,
            onExecute = onExecute,
            modifier = Modifier
                .fillMaxWidth()
                .height(15.pct)
        )

        Spacer(
            modifier = Modifier
                .height(1.dp)
                .background(Colour.gray)
        )

        SqlResultTable(
            records = state.table,
            header = state.tableHeader,
            isLoading = state.isLoading,
            error = state.error,
            onCellClick = onCellClick,
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
            IconButton(
                onClick = onReturn,
                modifier = Modifier
                    .fillMaxHeight()
                    .size(8.pct)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(1.pct)
                    )
                    .padding(1.dp)
            ) {
                BasicTextField(
                    value = sql,
                    onValueChange = onSqlChanged,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 12.sp,
                        lineHeight = 14.sp
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

            IconButton(
                onClick = onExecute,
                modifier = Modifier
                    .fillMaxHeight()
                    .size(8.pct)
                    .background(MaterialTheme.colorScheme.primary)
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
    isLoading: Boolean = false,
    error: String? = null,
    onCellClick: (record: DbRecord, columnName: String, currentValue: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = if (isLoading) "执行中…" else "执行结果 (${records.size} 条记录)",
            fontSize = 10.spwh,
            fontWeight = FontWeight.Bold,
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        error,
                        fontSize = 12.spwh,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            records.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "暂无数据",
                        fontSize = 12.spwh,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                PineZoomableTable(
                    columnCount = header.size,
                    lineCount = records.size,
                    renderCell = { rowIndex, columnIndex, scale ->
                        val record = records[rowIndex]
                        val columnName = header[columnIndex].name
                        val value = record.kvs[columnName]?.toString() ?: "NULL"
                        TableDataCell(
                            value = value,
                            scale = scale,
                            onClick = { onCellClick(record, columnName, value) }
                        )
                    },
                    header = { columnIndex, scale ->
                        TableHeaderCell(header[columnIndex].name, scale)
                    }
                )
            }
        }
    }
}

@Composable
fun TableDataCell(
    value: String,
    scale: Float,
    onClick: () -> Unit,
) {
    Text(
        text = value,
        fontSize = 8.sp * scale,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    )
}

@Composable
private fun TableHeaderCell(name: String, scale: Float) {
    Text(
        text = name,
        fontSize = 8.sp * scale,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.padding(4.dp)
    )
}

@Composable
fun EditCellDialog(
    columnName: String,
    initialValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember(initialValue) { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑: $columnName") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = false,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}


@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun RunSqlScreenPreview() {
    val status = RunSqlScreenStatus(
        dbName = "Default Database",
        tableName = "Default Table",
        sql = "SELECT * \n FROM table_name \n LIMIT 100",
        table = fakeDbRecords,
        tableHeader = fakeColumnInfos
    )

    RunSqlContent(
        state = status,
        onSqlChanged = {},
        onExecute = {},
        onReturn = {},
    )
}
