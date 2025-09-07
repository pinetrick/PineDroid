package com.pine.pinedroid.jetpack.ui.shopping

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pine.pinedroid.R
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.jetpack.ui.image.PineAsyncImage

@Composable
fun PineShoppingListItemVertical(
    modifier: Modifier = Modifier,
    image: OneImage? = null,
    title: String? = null,
    subtitle: String? = null,
    price: Double? = null,
    imageAspectRatio: Float = 1f, // 图片宽高比，默认1:1
    maxImageHeight: Dp = 180.dp, // 图片最大高度
    onItemClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onItemClick != null) { onItemClick?.invoke() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 商品图片 - 顶部，添加宽高比和最大高度限制
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(imageAspectRatio)
                    .height(maxImageHeight)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                PineAsyncImage(
                    model = image,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // 商品信息 - 底部
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 商品标题 - 中间
                title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 商品副标题 - 下面
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 商品价格 - 最下面
                price?.let {
                    Text(
                        text = "$${String.format("%.2f", it)}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun PineShoppingListItemVerticalPreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            // 展示不同宽高比的商品
            Text(
                text = "1:1 宽高比",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PineShoppingListItemVertical(
                    modifier = Modifier.weight(1f),
                    image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                    title = "Mac Book Pro",
                    subtitle = "Professional Laptop",
                    price = 2499.99,
                    imageAspectRatio = 1f, // 1:1 正方形
                    maxImageHeight = 120.dp
                )

                PineShoppingListItemVertical(
                    modifier = Modifier.weight(1f),
                    image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                    title = "iPhone 15",
                    subtitle = "Latest Smartphone",
                    price = 999.99,
                    imageAspectRatio = 1f,
                    maxImageHeight = 120.dp
                )
            }

            // 16:9 宽高比
            Text(
                text = "16:9 宽高比",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PineShoppingListItemVertical(
                    modifier = Modifier.weight(1f),
                    image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                    title = "TV 4K",
                    subtitle = "Ultra HD Television",
                    price = 899.99,
                    imageAspectRatio = 16f / 9f, // 16:9 宽屏
                    maxImageHeight = 100.dp
                )

                PineShoppingListItemVertical(
                    modifier = Modifier.weight(1f),
                    image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                    title = "Monitor",
                    subtitle = "Gaming Monitor",
                    price = 399.99,
                    imageAspectRatio = 16f / 9f,
                    maxImageHeight = 100.dp
                )
            }

            // 4:3 宽高比
            Text(
                text = "4:3 宽高比",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PineShoppingListItemVertical(
                    modifier = Modifier.weight(1f),
                    image = OneImage.Resource(R.drawable.pinedroid_image_loading),
                    title = "iPad Pro",
                    subtitle = "Tablet Computer",
                    price = 1099.99,
                    imageAspectRatio = 4f / 3f, // 4:3 传统比例
                    maxImageHeight = 130.dp
                )

                PineShoppingListItemVertical(
                    modifier = Modifier.weight(1f),
                    image = null,
                    title = "Camera",
                    subtitle = "Digital Camera",
                    price = 599.99,
                    imageAspectRatio = 4f / 3f,
                    maxImageHeight = 130.dp
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 180,
    heightDp = 280,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SinglePineShoppingListItemPreview() {
    MaterialTheme {
        PineShoppingListItemVertical(
            image = OneImage.Resource(R.drawable.pinedroid_image_loading),
            title = "Mac Book Pro 16-inch",
            subtitle = "Professional Laptop with M2 Chip",
            price = 2499.99,
            imageAspectRatio = 1f,
            maxImageHeight = 150.dp
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 160,
    heightDp = 220,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun CompactPineShoppingListItemPreview() {
    MaterialTheme {
        PineShoppingListItemVertical(
            image = OneImage.Resource(R.drawable.pinedroid_image_loading),
            title = "Watch",
            subtitle = "Smart Watch",
            price = 299.99,
            imageAspectRatio = 1f,
            maxImageHeight = 80.dp
        )
    }
}