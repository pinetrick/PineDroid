package com.pine.pinedroid.jetpack.ui.list

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pine.pinedroid.utils.ui.spwh

@Composable
fun PineHorizontalChoiceBar(
    horizontalChoiceBar: List<String>,
    selectedPage: Int = 0,
    onPageChoice: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()

    // 当 selectedPage 变化时，滚动到选中项
    LaunchedEffect(selectedPage) {
        scrollState.animateScrollToItem(selectedPage)
    }

    LazyRow(
        state = scrollState,
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(horizontalChoiceBar) { index, cityName ->
            CityItem(
                cityName = cityName,
                isSelected = index == selectedPage,
                onClick = { onPageChoice(index) }
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
            ) { onClick() }
            .padding(vertical = 8.dp), // 添加垂直内边距
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
                    .height(2.dp) // 增加下划线高度
                    .width((cityName.length * 8 + 8).dp) // 调整宽度计算
                    .background(selectedColor, shape = MaterialTheme.shapes.medium)
                    .padding(top = 4.dp) // 文字和下划线之间的间距
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
fun PreviewWithList() {
    Column {
        PineHorizontalChoiceBar(
            horizontalChoiceBar = PineHorizontalChoiceBarDemo,
            selectedPage = 1,
            onPageChoice = { }
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
fun PreviewWithMap() {
    Column {
        PineHorizontalChoiceBar(
            horizontalChoiceBar = PineHorizontalChoiceBarDemo,
            onPageChoice = { }
        )
    }
}

val PineHorizontalChoiceBarDemo =
    listOf("奥克兰", "惠灵顿", "ShenZhen", "HangZhou", "ShangHai", "GuangZhou")