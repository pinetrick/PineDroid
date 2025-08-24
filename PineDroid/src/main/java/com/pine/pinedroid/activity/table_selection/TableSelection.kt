package com.pine.pinedroid.activity.table_selection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.db.bean.TableInfo
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableSelection(
    navController: NavController? = null,
    dbName: String = "",
    viewModel: TableSelectionVM = viewModel()
) {
    // 处理导航
    HandleNavigation(navController = navController, viewModel = viewModel)

    // 收集状态
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    // 初始化加载（如果需要）
    LaunchedEffect(dbName) {
        if (dbName.isNotEmpty()) {
            viewModel.initialize(dbName)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = viewState.dbName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (navController?.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                viewState.tables.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    TableList(
                        tables = viewState.tables,
                        onTableClick = viewModel::onOpenTable
                    )
                }
            }
        }
    }
}

@Composable
fun TableList(
    tables: List<TableInfo>,
    onTableClick: (TableInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(tables) { table ->
            TableListItem(
                tableInfo = table,
                onClick = { onTableClick(table) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableListItem(
    tableInfo: TableInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = tableInfo.name,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = {
                if (tableInfo.rowCount != null) {
                    Text(
                        text = "行数: ${tableInfo.rowCount}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.TableChart,
                    contentDescription = "表图标"
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TableSelectionPreview() {

    TableSelection(
        dbName = "测试数据库",
    )

}

