package com.pine.pinedroid.activity.file_explorer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.R
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


@Composable
fun FileExplorer(
    navController: NavController? = null,
    viewModel: FileExplorerVM = viewModel()
) {
    HandleNavigation(navController = navController, viewModel = viewModel)

    val state by viewModel.viewState.collectAsState()

    LaunchedEffect(Unit) {
        if (state.currentDir == "/") {
            viewModel.initialize()
        }
    }

    FileExplorerScreen(
        state = state,
        storageLocations = viewModel.getStorageLocations(),
        onSelectLocation = { viewModel.selectLocation(it) },
        onNavigateToParent = { viewModel.navigateToParent() },
        onItemClick = { file ->
            if (file.isDirectory) viewModel.loadDirectory(file.absolutePath)
            else viewModel.loadFile(file.absolutePath)
        },
        onDataBases = { viewModel.onDatabase() },
        onRefresh = { viewModel.refresh() },
        onDelete = { viewModel.deleteFile(it) },
        onCreateFile = { viewModel.createFile(it) },
        onCreateFolder = { viewModel.createFolder(it) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileExplorerScreen(
    state: FileExplorerState,
    storageLocations: List<StorageLocation> = emptyList(),
    onSelectLocation: (String) -> Unit = {},
    onNavigateToParent: () -> Unit,
    onItemClick: (File) -> Unit,
    onDataBases: () -> Unit,
    onRefresh: () -> Unit,
    onDelete: (File) -> Unit,
    onCreateFile: (String) -> Unit,
    onCreateFolder: (String) -> Unit,
) {
    var showCreateMenu by remember { mutableStateOf(false) }
    var showCreateFileDialog by remember { mutableStateOf(false) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }

    if (showCreateFileDialog) {
        CreateItemDialog(
            title = stringResource(R.string.pine_file_explorer_new_file),
            placeholder = stringResource(R.string.pine_file_explorer_create_file_hint),
            onConfirm = { onCreateFile(it); showCreateFileDialog = false },
            onDismiss = { showCreateFileDialog = false }
        )
    }
    if (showCreateFolderDialog) {
        CreateItemDialog(
            title = stringResource(R.string.pine_file_explorer_new_folder),
            placeholder = stringResource(R.string.pine_file_explorer_create_folder_hint),
            onConfirm = { onCreateFolder(it); showCreateFolderDialog = false },
            onDismiss = { showCreateFolderDialog = false }
        )
    }

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
                    IconButton(onClick = onNavigateToParent) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.pine_file_explorer_back_cd))
                    }
                },
                actions = {
                    IconButton(onClick = onDataBases) {
                        Icon(Icons.Default.Storage, contentDescription = "DB")
                    }
                    // Create file/folder
                    Box {
                        IconButton(onClick = { showCreateMenu = true }) {
                            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.pine_file_explorer_new_cd))
                        }
                        DropdownMenu(
                            expanded = showCreateMenu,
                            onDismissRequest = { showCreateMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.pine_file_explorer_new_file)) },
                                leadingIcon = { Icon(Icons.Default.NoteAdd, null) },
                                onClick = { showCreateFileDialog = true; showCreateMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.pine_file_explorer_new_folder)) },
                                leadingIcon = { Icon(Icons.Default.CreateNewFolder, null) },
                                onClick = { showCreateFolderDialog = true; showCreateMenu = false }
                            )
                        }
                    }
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.pine_file_explorer_retry))
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
            if (state.showLocationPicker) {
                StorageLocationPicker(
                    locations = storageLocations,
                    onSelect = onSelectLocation,
                )
            } else if (state.isLoading) {
                LoadingIndicator()
            } else if (state.error != null) {
                ErrorView(error = state.error, onRetry = onRefresh)
            } else {
                FileList(
                    files = state.files,
                    onItemClick = onItemClick,
                    onDelete = onDelete,
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
            Text(stringResource(R.string.pine_file_explorer_retry))
        }
    }
}

@Composable
fun FileList(
    files: List<File>,
    onItemClick: (File) -> Unit,
    onDelete: (File) -> Unit,
) {
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
                    onClick = { onItemClick(file) },
                    onDelete = { onDelete(file) },
                )
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
            text = stringResource(R.string.pine_file_explorer_empty_dir),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileListItem(file: File, onClick: () -> Unit, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.pine_file_explorer_delete_title)) },
            text = { Text(stringResource(R.string.pine_file_explorer_delete_msg, file.name)) },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteConfirm = false }) {
                    Text(stringResource(R.string.pine_file_explorer_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text(stringResource(R.string.pine_file_explorer_cancel)) }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.pct)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { expanded = true }
            ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box {
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

                val fileInfoText = if (file.isDirectory) {
                    stringResource(R.string.pine_file_explorer_item_count, file.listFiles()?.size ?: 0)
                } else {
                    "${file.length().bToDisplayFileSize()} • " +
                            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                .format(Date(file.lastModified()))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = file.name,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = fileInfoText,
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

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.pine_file_explorer_copy_path)) },
                    onClick = {
                        clipboardManager.setText(AnnotatedString(file.absolutePath))
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.pine_file_explorer_delete), color = MaterialTheme.colorScheme.error) },
                    onClick = { showDeleteConfirm = true; expanded = false }
                )
            }
        }
    }
}

@Composable
fun CreateItemDialog(
    title: String,
    placeholder: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(placeholder) },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim()) },
                enabled = name.isNotBlank()
            ) { Text(stringResource(R.string.pine_file_explorer_create)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.pine_file_explorer_cancel)) }
        }
    )
}

@Composable
fun StorageLocationPicker(
    locations: List<StorageLocation>,
    onSelect: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(locations) { location ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.pct)
                    .combinedClickable(onClick = { onSelect(location.path) }),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.pct),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PineIcon(
                        text = location.icon,
                        fontSize = 24.spwh,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.pct),
                    )
                    Spacer(modifier = Modifier.width(3.pct))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = location.label, fontWeight = FontWeight.Medium)
                        Text(
                            text = location.path,
                            fontSize = 16.spwh,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
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
                    File("/data/data/com.example.app/files"),
                    File("/data/data/com.example.app/databases")
                )
            ),
            onNavigateToParent = {},
            onItemClick = {},
            onDataBases = {},
            onRefresh = {},
            onDelete = {},
            onCreateFile = {},
            onCreateFolder = {},
        )
    }
}
