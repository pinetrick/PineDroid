package com.pine.pinedroid.jetpack.ui.nav

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pine.pinedroid.jetpack.ui.button.PineButton
import com.pine.pinedroid.jetpack.ui.font.PineIcon
import com.pine.pinedroid.utils.ui.pct
import com.pine.pinedroid.utils.ui.pcth
import com.pine.pinedroid.utils.ui.spwh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PineTopAppBar(
    title: String,
    onReturn: (() -> Unit)? = null,
    actionIcon: String? = null,
    onAction: (() -> Unit)? = null,
    actionEnabled: Boolean = true,
    secondActionIcon: String? = null,
    onSecondAction: (() -> Unit)? = null
) {
    PineTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 20.spwh,


            )
        },
        navigationIcon = {
            onReturn?.let { onReturn ->
                IconButton(onClick = onReturn) {
                    PineIcon(
                        text = "\uf060",
                        fontSize = 20.spwh,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

        },
        actions = {
            secondActionIcon?.let { secondActionIcon ->
                IconButton(onClick = { }) {
                    PineIcon(
                        text = secondActionIcon,
                        fontSize = 20.spwh,
                        color = MaterialTheme.colorScheme.primary,
                        onClick = onSecondAction
                    )
                }
            }

            actionIcon?.let { actionIcon ->
                IconButton(onClick = { }, enabled = actionEnabled) {
                    PineIcon(
                        text = actionIcon,
                        fontSize = 20.spwh,
                        color = if (actionEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        onClick = if (actionEnabled) onAction else null
                    )
                }

            }


        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PineTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    expandedHeight: Dp = 12.pct,
    windowInsets: WindowInsets = WindowInsets(0, 0, 0, 0),
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        scrolledContainerColor = MaterialTheme.colorScheme.surface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface
    ),
    scrollBehavior: TopAppBarScrollBehavior? = null
) =
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        expandedHeight = expandedHeight,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior
    )





@Preview
@Composable
fun Preview1(){
    PineTopAppBar(
        title = "步道挑战",
        onReturn = {  },
        actionIcon = "\uf021",
        onAction = {  },
    )
}







@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Preview(){
    PineTopAppBar(
        title = {
            Text(
                text = "Text",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 24.spwh
            )
        },
        navigationIcon = {
            IconButton(onClick = {  }) {
                PineIcon(
                    text = "\uf060",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        actions = {
            // 设置按钮
            IconButton(onClick = {  }) {
                PineIcon(
                    text = "\uf060",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            // 收藏按钮
            IconButton(onClick = {  }) {
                PineIcon(
                    text = "\uf060",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

        },
    )
}