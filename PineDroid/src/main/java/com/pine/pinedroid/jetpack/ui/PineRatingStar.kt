package com.pine.pinedroid.jetpack.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview


/**
 * 状态提升版本的评分星星组件，允许外部控制评分状态
 * @param rating 当前评分值
 * @param onRatingChange 评分变化时的回调
 * @param maxRating 最大评分
 * @param color 星星颜色
 */
@Composable
fun PineRatingStar(
    rating: Float,
    onRatingChange: (Float) -> Unit = {},
    maxRating: Int = 5,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Row(modifier = Modifier.fillMaxSize()) {
        for (i in 1..maxRating) {
            Icon(
                imageVector = when {
                    i <= rating -> Icons.Filled.Star
                    i - 0.5f <= rating -> Icons.AutoMirrored.Filled.StarHalf
                    else -> Icons.Outlined.StarBorder
                },
                contentDescription = "评分 $i 星",
                tint = color,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clickable(
                        indication = null, // 去掉涟漪/背景变化
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        // 点击时设置评分
                        val newRating = when {
                            // 被点的这颗星星是整星就变成半星
                            i <= rating -> i - 0.5f
                            // 被点的这颗星星是半星或者空就变成整星
                            else -> i.toFloat()
                        }
                        onRatingChange(newRating)
                    }
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 100,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun RatingStarPreview() {
    PineRatingStar(rating = 2.5f, maxRating = 5)
}

