package com.pine.pinedroid.activity.text_editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.R
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextEditorScreen(
    navController: NavController? = null,
    filePath: String = "",
    viewModel: TextEditorVM = viewModel()
) {
    HandleNavigation(navController = navController, viewModel = viewModel)

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    // Local editor state
    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var searchIndex by remember { mutableIntStateOf(0) }
    var fontSize by remember { mutableIntStateOf(10) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    fun tryExit() {
        if (viewState.isModified) showExitDialog = true
        else navController?.navigateUp()
    }

    BackHandler { tryExit() }

    // Pre-resolve strings for use in LaunchedEffect / coroutines
    val confirmStr = stringResource(R.string.pine_text_editor_confirm)
    val saveSuccessStr = stringResource(R.string.pine_text_editor_save_success)
    val pathCopiedStr = stringResource(R.string.pine_text_editor_path_copied)

    // Compute search match positions
    val searchMatches = remember(searchQuery, viewState.content) {
        if (searchQuery.length < 2) return@remember emptyList<Int>()
        val result = mutableListOf<Int>()
        val text = viewState.content.lowercase()
        val q = searchQuery.lowercase()
        var idx = 0
        while (true) {
            val found = text.indexOf(q, idx)
            if (found == -1) break
            result.add(found)
            idx = found + 1
        }
        result
    }

    // Reset index when matches change
    LaunchedEffect(searchMatches) { searchIndex = 0 }

    // Sync textFieldValue when file loads
    LaunchedEffect(viewState.content) {
        if (textFieldValue.text != viewState.content) {
            textFieldValue = TextFieldValue(viewState.content)
        }
    }

    // Build annotated value for search highlighting
    val displayValue = remember(textFieldValue, searchMatches, searchIndex, searchQuery) {
        if (searchMatches.isEmpty() || searchQuery.length < 2) {
            textFieldValue
        } else {
            val annotated = buildAnnotatedString {
                append(textFieldValue.text)
                searchMatches.forEachIndexed { i, start ->
                    val end = (start + searchQuery.length).coerceAtMost(textFieldValue.text.length)
                    addStyle(
                        SpanStyle(
                            background = if (i == searchIndex)
                                Color(0xFFFFAA00).copy(alpha = 0.7f)
                            else
                                Color(0xFFFFFF00).copy(alpha = 0.4f)
                        ),
                        start, end
                    )
                }
            }
            TextFieldValue(annotated, textFieldValue.selection)
        }
    }

    fun jumpToMatch(index: Int) {
        if (searchMatches.isEmpty()) return
        val safe = ((index % searchMatches.size) + searchMatches.size) % searchMatches.size
        searchIndex = safe
        val pos = searchMatches[safe]
        textFieldValue = textFieldValue.copy(
            selection = TextRange(pos, (pos + searchQuery.length).coerceAtMost(textFieldValue.text.length))
        )
    }

    // Error snackbar
    viewState.error?.let { error ->
        LaunchedEffect(error) {
            val result = snackbarHostState.showSnackbar(
                message = error,
                actionLabel = confirmStr,
                withDismissAction = true
            )
            if (result == SnackbarResult.ActionPerformed) viewModel.clearError()
        }
    }

    // Save success — only show after an actual save (isSaving: false→true→false)
    var hadSaving by remember { mutableStateOf(false) }
    LaunchedEffect(viewState.isSaving) {
        if (hadSaving && !viewState.isSaving && viewState.error == null) {
            snackbarHostState.showSnackbar(saveSuccessStr)
        }
        if (viewState.isSaving) hadSaving = true
    }

    // Load file
    LaunchedEffect(filePath) {
        if (filePath.isNotEmpty()) viewModel.loadFile(filePath)
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("未保存的更改") },
            text = { Text("文件已修改，是否保存？") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    viewModel.saveFile()
                    navController?.navigateUp()
                }) { Text("保存") }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { showExitDialog = false }) { Text("取消") }
                    TextButton(onClick = {
                        showExitDialog = false
                        navController?.navigateUp()
                    }) { Text("不保存", color = MaterialTheme.colorScheme.error) }
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = viewState.fileName.ifEmpty { stringResource(R.string.pine_text_editor_title) },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 13.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { tryExit() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.pine_text_editor_back_cd))
                    }
                },
                actions = {
                    // Copy file path
                    if (viewState.filePath.isNotEmpty()) {
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(viewState.filePath))
                            coroutineScope.launch { snackbarHostState.showSnackbar(pathCopiedStr) }
                        }) {
                            Icon(Icons.Default.ContentCopy, stringResource(R.string.pine_text_editor_copy_path_cd))
                        }
                    }
                    // Search toggle
                    IconButton(onClick = {
                        isSearchVisible = !isSearchVisible
                        if (!isSearchVisible) searchQuery = ""
                    }) {
                        Icon(
                            imageVector = if (isSearchVisible) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = stringResource(R.string.pine_text_editor_search_cd)
                        )
                    }
                    // Save
                    if (viewState.isModified) {
                        IconButton(
                            onClick = { viewModel.saveFile() },
                            enabled = !viewState.isSaving && !viewState.isReadOnly
                        ) {
                            if (viewState.isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Filled.Save, stringResource(R.string.pine_text_editor_save_cd))
                            }
                        }
                    }
                    // More menu
                    Box {
                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "更多选项")
                        }
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("JSON 格式化") },
                                onClick = { viewModel.formatJson(); showMoreMenu = false }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("字体大小")
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(onClick = { if (fontSize > 10) fontSize-- }) {
                                                Text("A-", style = MaterialTheme.typography.labelLarge)
                                            }
                                            Text(
                                                "${fontSize}sp",
                                                modifier = Modifier.padding(horizontal = 4.dp),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            IconButton(onClick = { if (fontSize < 26) fontSize++ }) {
                                                Text("A+", style = MaterialTheme.typography.labelLarge)
                                            }
                                        }
                                    }
                                },
                                onClick = {}
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!viewState.isLoading) {
                FileStatusBar(viewState = viewState)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Search bar
            if (isSearchVisible) {
                SearchBar(
                    query = searchQuery,
                    matchCount = searchMatches.size,
                    currentIndex = searchIndex,
                    onQueryChange = { searchQuery = it },
                    onNext = { jumpToMatch(searchIndex + 1) },
                    onPrev = { jumpToMatch(searchIndex - 1) }
                )
            }

            // Content area
            Box(modifier = Modifier.weight(1f)) {
                when {
                    viewState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator()
                                Text(stringResource(R.string.pine_text_editor_loading))
                            }
                        }
                    }

                    viewState.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = stringResource(R.string.pine_text_editor_error_cd),
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = viewState.error ?: stringResource(R.string.pine_text_editor_unknown_error),
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                                Button(onClick = { viewModel.loadFile(viewState.filePath) }) {
                                    Text(stringResource(R.string.pine_text_editor_retry))
                                }
                            }
                        }
                    }

                    else -> {
                        val customTextSelectionColors = TextSelectionColors(
                            handleColor = MaterialTheme.colorScheme.primary,
                            backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                        )
                        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                BasicTextField(
                                    value = displayValue,
                                    onValueChange = {
                                        // Strip annotations, keep only text + selection
                                        textFieldValue = TextFieldValue(it.text, it.selection)
                                        viewModel.updateContent(it.text)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textStyle = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = fontSize.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    enabled = !viewState.isReadOnly && !viewState.isSaving,
                                    readOnly = viewState.isReadOnly,
                                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                )
                            }
                        }
                    }
                }

                // Saving overlay
                if (viewState.isSaving) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(stringResource(R.string.pine_text_editor_saving), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    matchCount: Int,
    currentIndex: Int,
    onQueryChange: (String) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(R.string.pine_text_editor_search_hint), style = MaterialTheme.typography.bodyMedium) },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium,
        )
        val noResultsStr = stringResource(R.string.pine_text_editor_no_results)
        val label = when {
            query.length < 2 -> ""
            matchCount == 0 -> noResultsStr
            else -> "${currentIndex + 1}/$matchCount"
        }
        if (label.isNotEmpty()) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 6.dp),
                style = MaterialTheme.typography.bodySmall,
                color = if (matchCount == 0) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onPrev, enabled = matchCount > 1) {
            Icon(Icons.Default.KeyboardArrowUp, stringResource(R.string.pine_text_editor_prev_cd))
        }
        IconButton(onClick = onNext, enabled = matchCount > 1) {
            Icon(Icons.Default.KeyboardArrowDown, stringResource(R.string.pine_text_editor_next_cd))
        }
    }
}

@Composable
fun FileStatusBar(viewState: TextEditorState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Divider()
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = viewState.encoding,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.pine_text_editor_lines, viewState.lineCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.pine_text_editor_words, viewState.wordCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.pine_text_editor_chars, viewState.charCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (viewState.isModified) {
                Text(
                    text = stringResource(R.string.pine_text_editor_modified),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTextEditorScreenReadOnly() {
    MaterialTheme {
        TextEditorScreen()
    }
}
