package com.pine.pinedroid.utils.ui

import androidx.compose.ui.unit.Dp

fun Float.dp2Px(): Int = (this * ScreenUtil.getDensity()).toInt()

fun Int.dp2Px(): Int = this.toFloat().dp2Px()

fun Dp.dp2Px(): Int = this.value.dp2Px()

fun Int.px2Dp(): Float = this / ScreenUtil.getDensity()
