package com.pine.pinedroid.activity.file_explorer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.jetpack.ui.font.PineIcon
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation
import com.pine.pinedroid.utils.file.bToDisplayFileSize
import com.pine.pinedroid.file.getFontAwesomeIcon
import com.pine.pinedroid.utils.ui.pct
import com.pine.pinedroid.utils.ui.spwh
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// FileExplorer.kt
@Composable
fun FileExplorer(
    navController: NavController? = null,
    viewModel: FileExplorerVM = viewModel()
) {
    // 处理导航
    HandleNavigation(navController = navController, viewModel = viewModel)

    // 收集状态
    val state by viewModel.viewState.collectAsState()

    // 初始化加载（如果需要）
    LaunchedEffect(Unit) {
        if (state.currentDir == "/") {
            viewModel.initialize()
        }
    }

    FileExplorerScreen(
        state = state,
        onNavigateToParent = { viewModel.navigateToParent() },
        onItemClick = { file ->
            if (file.isDirectory) {
                viewModel.loadDirectory(file.absolutePath)
            }
            else {
                viewModel.loadFile(file.absolutePath)
            }
        },
        onDataBases = {viewModel.onDatabase()},
        onRefresh = { viewModel.refresh() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileExplorerScreen(
    state: FileExplorerState,
    onNavigateToParent: () -> Unit,
    onItemClick: (File) -> Unit,
    onDataBases: () -> Unit,
    onRefresh: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.currentDir,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.spwh
                    )
                },
                navigationIcon = {
                    if (state.currentDir != "/") {
                        IconButton(onClick = onNavigateToParent) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回上级"
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onDataBases) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = "DB"
                        )
                    }
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "刷新"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading) {
                LoadingIndicator()
            } else if (state.error != null) {
                ErrorView(error = state.error, onRetry = onRefresh)
            } else {
                FileList(
                    currentDir = state.currentDir,
                    files = state.files,
                    onItemClick = onItemClick
                )
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(onClick = onRetry) {
            Text("重试")
        }
    }
}

@Composable
fun FileList(
    currentDir: String,
    files: List<File>,
    onItemClick: (File) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        if (files.isEmpty()) {
            EmptyDirectoryView()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(files) { file ->
                    FileListItem(
                        file = file,
                        onClick = { onItemClick(file) }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyDirectoryView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "空文件夹",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun FileListItem(file: File, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.pct)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.pct),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PineIcon(
                text = file.getFontAwesomeIcon(),
                fontSize = 24.spwh,
                color = if (file.isDirectory) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(14.pct),
            )

            Spacer(modifier = Modifier.width(3.pct))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (file.isDirectory) {
                        "文件夹"
                    } else {
                        "${file.length().bToDisplayFileSize()} • " +
                                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                    .format(Date(file.lastModified()))
                    },
                    fontSize = 16.spwh,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            PineIcon(
                text = "\uf105",
                fontSize = 24.spwh,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(16.pct),
            )



        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PreviewFileExplorer() {
    MaterialTheme {
        FileExplorerScreen(
            state = FileExplorerState(
                currentDir = "/data/data/com.example.app",
                files = listOf(
                    File("/data/data/com.example.app/files").apply {
                        // 模拟文件属性
                    },
                    File("/data/data/com.example.app/databases").apply {
                        // 模拟文件夹属性
                    }
                )
            ),
            onNavigateToParent = {},
            onItemClick = {},
            onDataBases = {},
            onRefresh = {}
        )
    }
}

@Preview
@Composable
fun PreviewFileListItem() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            FileListItem(
                file = File("/path/to/folder").apply {
                    // 模拟文件夹
                },
                onClick = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
            FileListItem(
                file = File("/path/to/file.txt").apply {
                    // 模拟文件
                },
                onClick = {}
            )
        }
    }
}
