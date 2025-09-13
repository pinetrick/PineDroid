package com.pine.pinedroid.utils

import com.pine.pinedroid.utils.log.loge

inline fun pineIgnoreExceptions(block: () -> Unit) {
    try {
        block()
    } catch (exception: Exception) {
        loge(exception)
    }
}