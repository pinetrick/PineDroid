package com.pine.pinedroid.utils.file

import java.util.Locale

fun Long.bToDisplayFileSize(): String {
    val b = this.toDouble()
    val kb = b / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0

    return when {
        gb >= 1 -> String.format(Locale.US, "%.2fG", gb).trimEnd('0').trimEnd('.')
        mb >= 1 -> String.format(Locale.US, "%.2fM", mb).trimEnd('0').trimEnd('.')
        kb >= 1 -> String.format(Locale.US, "%.2fK", kb).trimEnd('0').trimEnd('.')
        b >= 1 -> String.format(Locale.US, "%.2fB", b).trimEnd('0').trimEnd('.')
        else -> "0K"
    }
}


fun Long.kbToDisplayFileSize(): String {
    return (this * 1024).bToDisplayFileSize()
}

fun Int.kbToDisplayFileSize(): String = this.toLong().kbToDisplayFileSize()
