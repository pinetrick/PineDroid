package com.pine.pinedroid.utils.ui

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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