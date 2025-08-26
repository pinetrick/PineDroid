package com.pine.pinedroid.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Long.secondsToMinSec(): String {
    if (this < 0) return "00:00" // 处理负数情况

    val minutes = this / 60
    val seconds = this % 60

    return String.format("%02d:%02d", minutes, seconds)
}

fun Int.secondsToMinSec(): String {
    return this.toLong().secondsToMinSec()
}

fun Long.formatDate(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(Date(this))
}