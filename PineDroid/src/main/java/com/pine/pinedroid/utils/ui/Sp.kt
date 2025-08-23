package com.pine.pinedroid.utils.ui


import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlin.math.min

@Stable
inline val Int.spw: TextUnit
    get() {
        val screenWidth = ScreenUtil.getScreenWidthDp()
        val baseWidth = 768f
        return (this * screenWidth * 1.5f / baseWidth).sp
    }

@Stable
inline val Int.spwh: TextUnit
    get() {
        val screenWidth = ScreenUtil.getScreenWidthDp()
        val screenHeight = ScreenUtil.getScreenHeightDp()
        val baseWidth = 768f
        return (this * min(screenWidth, screenHeight) * 1.5f / baseWidth).sp
    }