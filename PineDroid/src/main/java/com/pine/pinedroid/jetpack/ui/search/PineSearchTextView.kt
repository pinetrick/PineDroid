package com.pine.pinedroid.jetpack.ui.search

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pine.pinedroid.jetpack.ui.font.PineIcon
import com.pine.pinedroid.jetpack.ui.modifier.pineBlinkAnimation
import com.pine.pinedroid.utils.ui.pct
import com.pine.pinedroid.utils.ui.spwh

@Composable
fun PineSearchTextView(
    modifier: Modifier = Modifier,

    searchText: String,
    placeHolder: String = "",
    onTextChange: (String) -> Unit = {},

    locationIcon: Boolean? = null,
    onLocationClicked: (() -> Unit)? = null,
    onPhotoPickup: (() -> Unit)? = null,
    onTakePhoto: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(13.pct)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 搜索图标
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(start = 12.dp)
                .size(20.dp)
        )

        // 搜索输入框
        BasicTextField(
            value = searchText,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            decorationBox = { innerTextField ->
                if (searchText.isEmpty()) {
                    Text(
                        text = placeHolder,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        style = LocalTextStyle.current,
                        fontSize = 20.spwh
                    )
                }
                innerTextField()
            },
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
        )

        // 清除按钮（当有文本时显示）
        if (searchText.isNotEmpty()) {
            IconButton(
                onClick = { onTextChange("") },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        locationIcon?.let { isEnabled ->
            PineIcon(
                text = "\uf3c5",
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clickable(
                        indication = null, // 去掉涟漪/背景变化
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onLocationClicked?.invoke() }
                    // 根据 locationIcon 的值添加闪烁效果
                    .then(if (!isEnabled) Modifier.pineBlinkAnimation() else Modifier),
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.5f
                )
            )
        }

        onPhotoPickup?.let {
            PineIcon(
                text = "\uf302",
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clickable(
                        indication = null, // 去掉涟漪/背景变化
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onPhotoPickup() },
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }

        onTakePhoto?.let {
            PineIcon(
                text = "\uf030",
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clickable(
                        indication = null, // 去掉涟漪/背景变化
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onTakePhoto() },
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }

    }
}


@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun PreviewNoRecord() {

    PineSearchTextView(
        searchText = "",
        locationIcon = true,
        onTakePhoto = {}
    )

}


