package com.pine.pinedroid.utils

import android.util.Log
import com.pine.pinedroid.db.DbRecord
import com.pine.pinedroid.db.Model

fun <T> log(content: T?) = logd(content)
fun <T> log(key: String, content: T?, level: Int = Log.DEBUG) {
    val output = when (content) {
        null -> "null"
        is String, is Number, is Boolean -> content.toString()
        else -> gson.toJson(content)
    }
    _log(key, output, level)
}

fun <T> logd(key: String, content: T?) = log(key, content, Log.DEBUG)
fun <T> loge(key: String, content: T?) = log(key, content, Log.ERROR)
fun <T> logi(key: String, content: T?) = log(key, content, Log.INFO)
fun <T> logw(key: String, content: T?) = log(key, content, Log.WARN)
fun <T> logv(key: String, content: T?) = log(key, content, Log.VERBOSE)


fun <T> logd(content: T?) = log("null", content, Log.DEBUG)
fun <T> loge(content: T?) = log("null", content, Log.ERROR)
fun <T> logi(content: T?) = log("null", content, Log.INFO)
fun <T> logw(content: T?) = log("null", content, Log.WARN)
fun <T> logv(content: T?) = log("null", content, Log.VERBOSE)



private fun _log(tag: String, content: String, level: Int = Log.DEBUG) {
    when (level) {
        Log.VERBOSE -> Log.v(tag, content)
        Log.DEBUG   -> Log.d(tag, content)
        Log.INFO    -> Log.i(tag, content)
        Log.WARN    -> Log.w(tag, content)
        Log.ERROR   -> Log.e(tag, content)
        else       -> Log.d(tag, content)  // 默认使用 DEBUG
    }
}