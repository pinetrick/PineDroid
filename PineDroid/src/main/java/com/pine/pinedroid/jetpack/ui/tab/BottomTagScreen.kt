package com.pine.pinedroid.jetpack.ui.tab

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.pine.pinedroid.jetpack.ui.font.FontAwesomeRegular
import com.pine.pinedroid.jetpack.ui.font.FontAwesomeSolid
import com.pine.pinedroid.jetpack.ui.font.PineIcon
import com.pine.pinedroid.utils.ui.pct
import com.pine.pinedroid.utils.ui.spwh


@Composable
fun BottomTagScreen(
    tabs: List<Tab>,
    onTabSelected: (Tab) -> Unit,
    block: @Composable (selectedTab: Tab) -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0), // 去掉自动加的内边距
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.pct) // 导航栏高度
                    .background(NavigationBarDefaults.containerColor),     // 背景色
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, title ->
                    NavItem(
                        navItem = title,
                        selected = title.isSelected,
                        onClick = onTabSelected,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
        content = { _ ->
            Box(modifier = Modifier.padding(bottom = 10.pct)) {
                val selectedTab = tabs.find { it.isSelected } ?: tabs.first()
                block(selectedTab)
            }

        }
    )
}

@Composable
fun NavItem(
    navItem: Tab,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (Tab) -> Unit = {},
) {
    // 定义选中和未选中的颜色
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    Column(
        modifier = modifier
            .clickable(
                indication = null, // 去掉涟漪/背景变化
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick(navItem) }
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        PineIcon(
            text = navItem.icon,
            modifier = Modifier.height(5.pct),
            color = if (selected) selectedColor else unselectedColor,
            fontFamily = if (selected) FontAwesomeSolid else FontAwesomeRegular
        )
        Text(
            text = navItem.title,
            fontSize = 15.spwh,
            color = if (selected) selectedColor else unselectedColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )


    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewDark() {
    BottomTagScreen(
        listOf(
            Tab("Home", "\uf015", true),
            Tab("Profile", "\uf007", false),
        ), {}, { _ -> }
    )

}

