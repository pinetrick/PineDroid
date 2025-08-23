package com.pine.pinedroid.utils.ui

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Stable
inline val Int.pctw: Dp
    get() {
        val screenWidth = ScreenUtil.getScreenWidthDp()
        return (screenWidth * this / 100f).dp
    }

@Stable
inline val Int.pcth: Dp
    get() {
        val screenHeight = ScreenUtil.getScreenHeightDp()
        return (screenHeight * this / 100f).dp
    }

@Stable
inline val Int.pct: Dp
    get() {
        val screenHeight = ScreenUtil.getScreenHeightDp()
        val screenWidth = ScreenUtil.getScreenWidthDp()
        return (min(screenWidth, screenHeight) * this / 100f).dp
    }
