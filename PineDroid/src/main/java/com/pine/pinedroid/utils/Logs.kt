package com.pine.pinedroid.utils

import android.util.Log
import com.pine.pinedroid.db.Model


fun <T> log(key: String, content: T?) {
    val output = when (content) {
        null -> "null"
        is String, is Number, is Boolean -> content.toString()
        is Model -> content.log()
        else -> gson.toJson(content)
    }
    _log(key, output)
}

fun <T> log(content: T?) {
    log("null", content)
}

private fun _log(tag: String, content: String) {
    Log.d(tag, content)
}