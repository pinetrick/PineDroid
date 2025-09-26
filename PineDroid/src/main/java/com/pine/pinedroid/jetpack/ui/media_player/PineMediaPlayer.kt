package com.pine.pinedroid.jetpack.ui.media_player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pine.pinedroid.activity.image_pickup.OneImage
import com.pine.pinedroid.jetpack.ui.button.PineButton
import com.pine.pinedroid.jetpack.ui.image.PineAsyncImage
import com.pine.pinedroid.utils.ui.pct
import com.pine.pinedroid.utils.ui.spwh


@Composable
fun PineMediaPlayer(
    image: OneImage?,
    title: String?,
    description: String?,
    modifier: Modifier = Modifier,
    onImageClick: ((Any?) -> Unit)? = null,
    onLast: (() -> Unit)? = null,
    onPlay: (() -> Unit)? = null,
    onNext: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左边图片 - 圆角
        PineAsyncImage(
            model = image, // 替换为你的图片资源
            modifier = Modifier
                .size(15.pct)
                .clip(RoundedCornerShape(5.dp)),
            contentScale = ContentScale.Crop,
            onClicked = onImageClick
        )

        // 中间标题和描述
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp, end = 5.dp),
            verticalArrangement = Arrangement.Center
        ) {
            title?.let {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    fontSize = 20.spwh,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }


            description?.let {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    fontSize = 12.spwh,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        }

        // 右边控制按钮
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 上一曲按钮
            PineButton(
                icon = "\uf04a",
                enabled = onLast != null,
                onClick = onLast,
                fontSize = 20.spwh,
                border = null,
            )

            PineButton(
                icon = "\uf04b",
                enabled = onPlay != null,
                onClick = onPlay,
                fontSize = 30.spwh,
                border = null,
            )

            PineButton(
                icon = "\uf04e",
                enabled = onNext != null,
                onClick = onNext,
                fontSize = 20.spwh,
                border = null,
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 72,
)
@Composable
fun PlayerPreview() {
    PineMediaPlayer(
        image = OneImage.HttpImage("https://picsum.photos/200/300"),
        title = "景点导览标题",
        description = "景点导览描述信息，这里可以显示详细的描述内容",
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    )
}