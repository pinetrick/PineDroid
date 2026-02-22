@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.pine.pinedroid.debug.http_queue.list_screen

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.jetpack.ui.font.PineIcon
import com.pine.pinedroid.jetpack.ui.image.PineAsyncImage
import com.pine.pinedroid.jetpack.ui.loading.PineLoading
import com.pine.pinedroid.jetpack.ui.nav.PineGeneralScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation
import com.pine.pinedroid.net.http_queue.bean.PendingPostRequest
import com.pine.pinedroid.utils.pineToString

@Composable
fun UploadQueueScreen(
    navController: NavController? = null,
    viewModel: UploadQueueVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    HandleNavigation(navController = navController, viewModel = viewModel) {
        viewModel.onInit()
    }

    PineLoading(viewState.isLoading) {
        PineGeneralScreen(
            title = {
                PineTopAppBar(
                    title = { Text("Upload Queue", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = viewModel::navigateBack) {
                            PineIcon(text = "\uf060", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    actions = {
                        IconButton(onClick = viewModel::clearAll) {
                            PineIcon(text = "\uf1f8", fontSize = 20.sp, color = MaterialTheme.colorScheme.error)
                        }
                        IconButton(onClick = viewModel::processQueue) {
                            PineIcon(text = "\uf093", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = viewModel::onInit) {
                            PineIcon(text = "\uf021", fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            },
            content = {
                Content(viewModel, viewState)
            },
        )
    }
}

@Composable
fun Content(viewModel: UploadQueueVM, viewState: UploadQueueState) {
    when {
        viewState.queues.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PineIcon(
                        text = "\uf0ee",
                        fontSize = 56.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "No pending uploads",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = viewState.queues,
                    key = { it.id ?: -1 }
                ) { request ->
                    UploadQueueItem(
                        request = request,
                        onDelete = { viewModel.deleteRequest(request) },
                        onUpload = { viewModel.uploadRequest(request) }
                    )
                }
            }
        }
    }
}

@Composable
fun UploadQueueItem(
    request: PendingPostRequest,
    onDelete: () -> Unit,
    onUpload: () -> Unit
) {
    val isPost = request.is_post
    val methodColor = if (isPost) Color(0xFF2E7D32) else Color(0xFF1565C0)
    val methodBg = if (isPost) Color(0xFFE8F5E9) else Color(0xFFE3F2FD)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // ── Header ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Method badge
                Text(
                    text = if (isPost) "POST" else "GET",
                    style = MaterialTheme.typography.labelSmall,
                    color = methodColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(methodBg)
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                )

                // URL
                Text(
                    text = request.url,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Retry badge
                if (request.retry_count > 0) {
                    Text(
                        text = "×${request.retry_count}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // ── Data block ──────────────────────────────────────────
            if (request.data.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        request.data.forEach { (key, value) ->
                            Row {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = ": ",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = value.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                if (request.local_files.isNotEmpty()) {
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // ── File previews ───────────────────────────────────────
            if (request.local_files.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(request.local_files.entries.toList()) { (key, filePath) ->
                        FilePreviewItem(key = key, filePath = filePath)
                    }
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // ── Footer: time + actions ───────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.next_time.pineToString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                    modifier = Modifier.weight(1f)
                )

                // Delete button
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }

                // Upload button
                TextButton(onClick = onUpload) {
                    Icon(
                        imageVector = Icons.Default.Upload,
                        contentDescription = "Upload",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun FilePreviewItem(key: String, filePath: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.size(72.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isImageFile(filePath)) {
                    PineAsyncImage(
                        model = OneImage.LocalImage(filePath),
                        contentDescription = "File preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = painterResource(android.R.drawable.ic_menu_report_image),
                        placeholder = painterResource(android.R.drawable.ic_menu_gallery)
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        PineIcon(
                            text = "\uf1c5",
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = getFileExtension(filePath),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            Text(
                text = key,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}

private fun isImageFile(filePath: String): Boolean {
    val imageExtensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp")
    return imageExtensions.any { filePath.lowercase().endsWith(it) }
}

private fun getFileExtension(filePath: String): String {
    return filePath.substringAfterLast('.', "").uppercase()
}


@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewDark() {
    UploadQueueScreen()
}
