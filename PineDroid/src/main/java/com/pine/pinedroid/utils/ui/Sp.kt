package com.pine.pinedroid.utils.ui


import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Stable
inline val Int.spw: TextUnit
    get() {
        val screenWidth = ScreenUtil.getScreenWidthDp()
        val baseWidth = 768f
        return (this * screenWidth * 1.5f / baseWidth).sp
    }