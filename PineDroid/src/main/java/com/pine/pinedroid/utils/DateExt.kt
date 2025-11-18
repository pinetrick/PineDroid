package com.pine.pinedroid.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.pineToString(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(this)
}



fun Long.secondsToTime(format: String = "HH:mm:ss"): String {
    if (this < 0) return "00:00" // 处理负数情况

    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    return when (format) {
        "HH:mm:ss" -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
        "mm:ss" -> String.format("%02d:%02d", minutes, seconds)
        "yyyy-MM-dd HH:mm:ss" -> {
            // 对于完整日期时间格式，需要将秒数转换为Date对象
            val date = Date(this * 1000) // 转换为毫秒
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            sdf.format(date)
        }
        else -> throw IllegalArgumentException("不支持的格式: $format")
    }
}

fun Int.secondsToTime(format: String = "HH:mm:ss"): String {
    return this.toLong().secondsToTime(format)
}

fun Long.formatDate(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(Date(this))
}

