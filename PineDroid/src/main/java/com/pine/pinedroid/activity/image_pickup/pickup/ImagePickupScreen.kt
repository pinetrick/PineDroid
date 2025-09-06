package com.pine.pinedroid.activity.image_pickup.pickup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.pine.pinedroid.R
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.jetpack.ui.button.PineButton
import com.pine.pinedroid.jetpack.ui.font.PineIcon
import com.pine.pinedroid.jetpack.ui.image.PineAsyncImage
import com.pine.pinedroid.jetpack.ui.loading.PineLoading
import com.pine.pinedroid.jetpack.ui.nav.GeneralPineScreen
import com.pine.pinedroid.jetpack.ui.nav.PineTopAppBar
import com.pine.pinedroid.jetpack.ui.score_bar.PineScrollIndicator
import com.pine.pinedroid.jetpack.viewmodel.HandleNavigation
import com.pine.pinedroid.utils.ui.pct
import com.pine.pinedroid.utils.ui.spwh

@Composable
fun ImagePickupScreen(
    navController: NavHostController? = null,
    viewModel: ImagePickupScreenVM = viewModel()
) {
    HandleNavigation(navController = navController, viewModel = viewModel)

    val viewState by viewModel.viewState.collectAsState()

    // 模拟加载图片数据（实际应用中应该从媒体库加载）
    LaunchedEffect(Unit) {
        viewModel.runOnce {
            viewModel.onInit()
        }
    }
    if (viewState.loading) {
        PineLoading(stringResource(R.string.pine_image_pickup_processing))
    } else {
        GeneralPineScreen(
            title = {
                Title(
                    totalAccount = viewState.selectedImages.size,
                    onBack = viewModel::onReturnClick,
                    viewModel::onComplete
                )
            },
            content = {
                Content(
                    viewState,
                    viewModel::onSelectChange,
                    viewModel::onImageClicked,
                    viewModel::onTakePhoto
                )
            },
        )
    }


}

@Composable
fun Content(
    viewState: ImagePickupScreenState,
    onSelectChange: (OneImage) -> Unit,
    onImageClicked: (OneImage) -> Unit,
    onTakePhoto: (OneImage) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val scrollState = rememberLazyGridState()

        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                modifier = Modifier.PineScrollIndicator(scrollState),
                state = scrollState,
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (viewState.enabledCamera) {
                    item(null) {
                        ImageGridItem(
                            oneImage = OneImage.Resource(R.drawable.camera),
                            allowSelection = false,
                            onImageClicked = onTakePhoto,
                        )
                    }
                }
                items(
                    items = viewState.imageUris,
                    key = { it.hashCode().toString() }
                ) { oneImage ->
                    ImageGridItem(
                        oneImage = oneImage,
                        allowSelection = true,
                        isSelected = viewState.selectedImages.contains(oneImage),
                        onSelectChange = onSelectChange,
                        onImageClicked = onImageClicked,
                    )
                }
            }


        }


    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Title(
    totalAccount: Int,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    PineTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.pine_image_pickup_title) + if (totalAccount > 0) "(${totalAccount})" else "",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 24.spwh
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                PineIcon(
                    text = "\uf060",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        actions = {
            PineButton(
                text = stringResource(R.string.pine_confirm),
                onClick = onConfirm,
                enabled = totalAccount > 0,
                modifier = Modifier.padding(end = 3.pct)
            )

        },
    )
}

@Composable
fun ImageGridItem(
    oneImage: OneImage,
    allowSelection: Boolean = true,
    isSelected: Boolean = false,
    onSelectChange: (OneImage) -> Unit = {},
    onImageClicked: (OneImage) -> Unit = {},
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
    ) {
        // 图片 - 点击显示大图
        PineAsyncImage(
            model = oneImage,
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onImageClicked(oneImage)
                },
            contentScale = ContentScale.Crop
        )

        // 选择框 - 只在点击选择框时触发选中
        if (allowSelection) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(25.dp)
                    .clickable {
                        onSelectChange(oneImage)
                    }
            ) {
                // 选择框背景
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.White.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                        .border(
                            width = 2.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.Gray.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                )

                // 选中时的对勾图标
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(15.dp)
                    )
                } else {
                    // 未选中时的圆圈图标（可选）
                    Spacer(
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    ImagePickupScreen()
}