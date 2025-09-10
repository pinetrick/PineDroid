package com.pine.pinedroid.jetpack.ui.list.shopping

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pine.pinedroid.jetpack.ui.PineIsScreenPortrait
import com.pine.pinedroid.jetpack.ui.image.PineAsyncImage
import com.pine.pinedroid.jetpack.ui.list.vertical_grid.pineScrollIndicator
import com.pine.pinedroid.utils.ui.pct
import com.pine.pinedroid.utils.ui.spwh


@Composable
fun PineShoppingListItemVertical(
    modifier: Modifier = Modifier,
    shoppingItemBean: PineShoppingItemBean = PineShoppingItemBean(),
    maxImageHeight: Dp = 180.dp, // 图片最大高度
    onItemClick: ((PineShoppingItemBean) -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onItemClick != null) { onItemClick?.invoke(shoppingItemBean) },
        shape = RoundedCornerShape(1.pct),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 商品图片 - 顶部，添加宽高比和最大高度限制
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(shoppingItemBean.imageAspectRatio)
                    .height(maxImageHeight)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.TopEnd
            ) {
                PineAsyncImage(
                    model = shoppingItemBean.image,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                shoppingItemBean.textOnImage?.let { textOnImage ->
                    Text(
                        text = textOnImage,
                        fontSize = 13.spwh,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .padding(2.pct)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 1.pct),
                    )
                }
            }

            // 商品信息 - 底部
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.pct, end = 2.pct, bottom = 2.pct),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 商品标题 - 中间
                shoppingItemBean.title?.let {
                    Text(
                        text = it,
                        fontSize = 17.spwh,
                        lineHeight = 17.spwh,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                // 商品副标题 - 下面
                shoppingItemBean.subtitle?.let {
                    Text(
                        text = it,
                        fontSize = 12.spwh,
                        lineHeight = 12.spwh,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }

                // 商品价格 - 最下面
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        shoppingItemBean.priceUnit?.let { priceUnit ->
                            Text(
                                text = priceUnit,
                                fontSize = 15.spwh,
                                lineHeight = 15.spwh,
                                modifier = Modifier.padding(bottom = 1.dp),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        }
                        shoppingItemBean.price?.let { price ->
                            Text(
                                text = String.format("%.2f", price),
                                fontSize = 22.spwh,
                                lineHeight = 22.spwh,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }

                    shoppingItemBean.priceHint?.let { priceHint ->
                        Text(
                            text = priceHint,
                            fontSize = 12.spwh,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

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
        val scrollState = rememberLazyGridState()
        LazyVerticalGrid(
            modifier = Modifier.pineScrollIndicator(scrollState),
            state = scrollState,
            columns = GridCells.Fixed(if (PineIsScreenPortrait()) 2 else 4),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            items(PineShoppingItemBean.ShoppingItemBeanDemo) { destination ->
                PineShoppingListItemVertical(
                    shoppingItemBean = destination,
                    onItemClick = { }
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
            shoppingItemBean = PineShoppingItemBean.ShoppingItemBeanDemo[0],
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
            shoppingItemBean = PineShoppingItemBean.ShoppingItemBeanDemo[1],
            maxImageHeight = 80.dp
        )
    }
}
