package com.pine.pinedroid.jetpack.ui

import android.app.Activity
import android.os.Build
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun SyncSystemNavBarWithBottomBar() {
    val view = LocalView.current
    // 底部导航栏你用的背景色
    val navBarColor: Color = NavigationBarDefaults.containerColor
    val useDarkIcons = navBarColor.luminance() > 0.5f

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // 不启用 edge-to-edge（避免额外 padding），只改系统栏颜色
            window.navigationBarColor = navBarColor.toArgb()
            // 关掉对比度强制的那层半透明遮罩，避免上缘出现一条阴影/分割线
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
            // 有些机型会在导航栏上方加 divider，把它也统一掉（API 28+）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                window.navigationBarDividerColor = navBarColor.toArgb()
            }
            // 根据背景亮度切换图标深浅
            WindowCompat.getInsetsController(window, window.decorView)
                .isAppearanceLightNavigationBars = useDarkIcons
        }
    }
}