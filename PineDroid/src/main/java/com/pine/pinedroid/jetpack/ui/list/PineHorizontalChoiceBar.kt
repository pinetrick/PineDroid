package com.pine.pinedroid.jetpack.ui.list

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pine.pinedroid.utils.ui.spwh

@Composable
fun PineHorizontalChoiceBar(
    horizontalChoiceBar: Map<String, Boolean>,
    onCityChoice: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        horizontalChoiceBar.forEach { (cityName, isSelected) ->
            CityItem(
                cityName = cityName,
                isSelected = isSelected,
                onClick = { onCityChoice(cityName) }
            )
        }
    }
}

@Composable
fun CityItem(
    cityName: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    val textColor = if (isSelected) selectedColor else unselectedColor
    val fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal

    Column(
        modifier = modifier
            .clickable(
                indication = null, // 去掉涟漪/背景变化
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally, // 水平居中
        verticalArrangement = Arrangement.Center // 垂直居中
    ) {
        Text(
            text = cityName,
            fontSize = 15.spwh,
            lineHeight = 15.spwh,
            color = textColor,
            fontWeight = fontWeight,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .width((cityName.length * 4).dp)
                    .background(selectedColor, shape = MaterialTheme.shapes.medium) // 更圆的角
            )
        }
    }


}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun Preview() {
    Column {
        PineHorizontalChoiceBar(PineHorizontalChoiceBarDemo,
            { }
        )
    }
}


val PineHorizontalChoiceBarDemo = mapOf(
    "奥克兰" to true,
    "惠灵顿" to false,
    "ShenZhen" to false,
    "HangZhou" to true,
    "ShangHa1i" to false,
    "GuangZh1ou" to false,
    "ShenZ2hen" to false,
    "HangZ3hou" to false,
)
