package com.pine.pinedroid.jetpack.ui.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pine.pinedroid.jetpack.ui.font.PineIcon
import com.pine.pinedroid.utils.ui.spwh

@Composable
fun PineButton(
    text: String? = null,
    icon: String? = null,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent, // 透明背景
        contentColor = MaterialTheme.colorScheme.primary, // 文字颜色
        disabledContainerColor = Color.Transparent, // 禁用时透明背景
        disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f) // 禁用时文字颜色
    ),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(
        defaultElevation = 0.dp, // 去除默认阴影
        pressedElevation = 0.dp,
        disabledElevation = 0.dp
    ),
    border: BorderStroke? = BorderStroke(
        width = 2.dp,
        color = if (enabled) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
    ),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
) {

    // 打卡按钮
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = shape,
        elevation = elevation,
        colors = colors,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    ) {
        icon?.let { icon ->
            PineIcon(
                text = icon,
                fontSize = 18.spwh,
            )

        }

        if (icon != null && text != null){
            Spacer(modifier = Modifier.width(8.dp))
        }

        text?.let { text ->
            Text(text = text, fontSize = 18.spwh)
        }
    }


}


@Preview
@Composable
fun Preview() {
    PineButton(
        "确定",
        icon = "\uf4fc",
    )
}