package com.pine.pinedroid.activity.image_pickup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.pine.pinedroid.R
import com.pine.pinedroid.utils.appContext
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePickupScreen(
    allowCamera: Boolean,
    allowMultiple: Boolean,
    onBack: () -> Unit,
    onComplete: (List<Uri>) -> Unit,
    viewModel: ImagePickupScreenVM = viewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    // 模拟加载图片数据（实际应用中应该从媒体库加载）
    LaunchedEffect(Unit) {
        viewModel.onInit()
    }

    // 拍照启动器
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            // 拍照成功后的处理
            // 这里需要重新加载图片列表
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("选择图片") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            if (allowCamera) {
                FloatingActionButton(
                    onClick = {
                        // 创建临时文件用于拍照
                        val tempFile = File.createTempFile(
                            "IMG_${System.currentTimeMillis()}",
                            ".jpg",
                            appContext.externalCacheDir
                        )
                        takePictureLauncher.launch(tempFile.toUri())
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "拍照")
                }
            }
        },
        bottomBar = {
            BottomActionBar(
                selectedCount = viewState.selectedImages.size,
                onComplete = { onComplete(viewState.selectedImages.toList()) },
                enabled = viewState.selectedImages.isNotEmpty()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 选择计数
            Text(
                text = "已选择 ${viewState.selectedImages.size} 张图片",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            // 图片网格
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(viewState.imageUris) { uri ->
                    ImageGridItem(
                        imageUri = uri,
                        isSelected = viewState.selectedImages.contains(uri),
                        allowMultiple = allowMultiple,
                        onSelectChange = { selected ->
                            if (selected) {
                                if (allowMultiple) {
                                    viewState.selectedImages.add(uri)
                                } else {
                                    viewState.selectedImages.clear()
                                    viewState.selectedImages.add(uri)
                                }
                            } else {
                                viewState.selectedImages.remove(uri)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ImageGridItem(
    imageUri: Uri,
    isSelected: Boolean,
    allowMultiple: Boolean,
    onSelectChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                if (allowMultiple) {
                    onSelectChange(!isSelected)
                } else {
                    onSelectChange(true)
                }
            }
    ) {
        // 图片
        Image(
            painter = rememberAsyncImagePainter(
                model = imageUri,
                error = painterResource(R.drawable.pinedroid_image_off) // 需要添加占位图
            ),
            contentDescription = "图片",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 选择指示器
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "已选择",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun BottomActionBar(
    selectedCount: Int,
    onComplete: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "已选择 $selectedCount 项",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Button(
            onClick = onComplete,
            enabled = enabled
        ) {
            Text("完成")
        }
    }
}

// 占位图资源（需要在res/drawable中添加）
// ic_image_placeholder.xml 可以是一个简单的图片占位符