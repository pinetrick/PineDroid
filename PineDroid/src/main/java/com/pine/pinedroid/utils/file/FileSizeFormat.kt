package com.pine.pinedroid.utils.file

import java.util.Locale

fun Long.kbToDisplayFileSize(): String {
    val kb = this.toDouble()
    val mb = kb / 1024.0
    val gb = mb / 1024.0

    return when {
        gb >= 1 -> String.format(Locale.US, "%.2fG", gb).trimEnd('0').trimEnd('.')
        mb >= 1 -> String.format(Locale.US, "%.2fM", mb).trimEnd('0').trimEnd('.')
        kb >= 1 -> String.format(Locale.US, "%.2fK", kb).trimEnd('0').trimEnd('.')
        else -> "0K"
    }
}

fun Int.kbToDisplayFileSize(): String = this.toLong().kbToDisplayFileSize()
