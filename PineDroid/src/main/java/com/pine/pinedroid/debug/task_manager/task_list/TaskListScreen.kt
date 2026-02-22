@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.pine.pinedroid.debug.task_manager.task_list


import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.R
import com.pine.pinedroid.jetpack.ui.nav.PineGeneralScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation

@Composable
fun TaskListScreen(
    navController: NavController? = null,
    viewModel: TaskListScreenVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    val context = LocalContext.current
    val title = stringResource(R.string.pine_task_list_title)

    HandleNavigation(navController = navController, viewModel = viewModel) {
        viewModel.onInit(context)
    }

    PineGeneralScreen(
        title = {
            PineTopAppBar(
                title = title,
                onReturn = viewModel::navigateBack,
                actionIcon = "\uf2f9",
                onAction = { viewModel.onRefresh() },
            )
        },
        content = {
            Content(viewModel, viewState)
        },
    )
}

@Composable
fun Content(viewModel: TaskListScreenVM, viewState: TaskListScreenState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MemoryOverviewCard(memoryInfo = viewState.memoryInfo)

        ProcessList(
            apps = viewState.apps,
            isLoading = viewState.isLoading,
            onRefresh = { viewModel.onRefresh() },
            onKillClick = { viewModel.onKillClick(it) }
        )
    }

    // Kill confirm dialog
    viewState.processToKill?.let { app ->
        KillConfirmDialog(
            app = app,
            onConfirm = { viewModel.onKillConfirm() },
            onDismiss = { viewModel.onKillDismiss() }
        )
    }
}

@Composable
fun KillConfirmDialog(app: AppInfos, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = { Text(stringResource(R.string.pine_task_list_kill_title)) },
        text = {
            Column {
                Text(stringResource(R.string.pine_task_list_process, app.name))
                Text(stringResource(R.string.pine_task_list_pid_category, app.pid, app.category), style = MaterialTheme.typography.bodySmall)
                if (app.isMainProcess) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.pine_task_list_main_process_warning),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.pine_task_list_kill), color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.pine_task_list_cancel)) }
        }
    )
}

@Composable
fun MemoryOverviewCard(memoryInfo: MemoryInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.pine_task_list_memory_overview),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            MemoryInfoItem(stringResource(R.string.pine_task_list_total_ram), memoryInfo.totalRAM)
            MemoryInfoItem(stringResource(R.string.pine_task_list_free_ram), memoryInfo.freeRAM)
            MemoryInfoItem(stringResource(R.string.pine_task_list_used_ram), memoryInfo.usedRAM)
            MemoryInfoItem(stringResource(R.string.pine_task_list_low_mem_threshold), memoryInfo.lostRAM)
            MemoryInfoItem(stringResource(R.string.pine_task_list_zram), memoryInfo.zramInfo)
        }
    }
}

@Composable
fun MemoryInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ProcessList(
    apps: List<AppInfos>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onKillClick: (AppInfos) -> Unit,
) {
    var expandedItems by remember { mutableStateOf(setOf<String>()) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.pine_task_list_running_processes, apps.size),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.pine_task_list_refresh),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Android 10+ note
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 1.dp)
            )
            Text(
                text = stringResource(R.string.pine_task_list_android10_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(apps) { app ->
                    ProcessItem(
                        app = app,
                        isExpanded = expandedItems.contains("${app.pid}-${app.name}"),
                        onExpandedChange = { expanded ->
                            val key = "${app.pid}-${app.name}"
                            expandedItems = if (expanded) expandedItems + key else expandedItems - key
                        },
                        onKillClick = { onKillClick(app) }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun ProcessItem(
    app: AppInfos,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onKillClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onExpandedChange(!isExpanded) }
    ) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = app.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (app.isMainProcess) {
                            Text(
                                text = stringResource(R.string.pine_task_list_main_badge),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = MaterialTheme.shapes.extraSmall
                                    )
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        ProcessDetail(app = app)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = app.pss,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(onClick = onKillClick) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.pine_task_list_kill_cd),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (!isExpanded) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.pine_task_list_pid, app.pid),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = app.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ProcessDetail(app: AppInfos) {
    Column {
        DetailRow(stringResource(R.string.pine_task_list_detail_pid), app.pid)
        DetailRow(stringResource(R.string.pine_task_list_detail_package), app.packageName)
        DetailRow(stringResource(R.string.pine_task_list_detail_type), app.category)
        DetailRow(stringResource(R.string.pine_task_list_detail_memory), app.pss)
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewDark() {
    TaskListScreen()
}
