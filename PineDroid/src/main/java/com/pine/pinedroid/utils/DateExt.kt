package com.pine.pinedroid.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun Long.formatDate(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(Date(this))
}