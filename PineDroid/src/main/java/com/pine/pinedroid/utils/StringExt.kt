package com.pine.pinedroid.utils

import androidx.annotation.StringRes

fun String.camelToSnakeCase(): String {
    return this
        .replace(Regex("([a-z0-9])([A-Z])"), "$1_$2") // 小写/数字 + 大写 → 加下划线
        .replace(Regex("([A-Z]+)([A-Z][a-z])"), "$1_$2") // 连续大写后面接小写 → 拆开
        .lowercase()
}

