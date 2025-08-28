package com.pine.pinedroid.utils.log

import android.util.Log
import com.pine.pinedroid.utils.ToString
import com.pine.pinedroid.utils.gson

fun <T> log(content: T?) = logd(content)
fun <T> log(key: String, content: T?, level: Int = Log.DEBUG) {
    val output = try {
        when (content) {
            null -> "null"
            is String, is Number, is Boolean -> content.toString()
            is Exception -> (content.cause?.toString() ?: content.toString()).also { content.printStackTrace()  }
            is ByteArray -> content.ToString()
            is List<*> -> content.ToString(30)
            is Map<*, *> -> content.ToString(30)
            else -> gson.toJson(content)
        }
    } catch (e: Exception) {
        e.toString()
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
    val finalTag = tag.ifEmpty {
        // 获取调用者的类名
        Thread.currentThread().stackTrace
            .firstOrNull { !it.className.contains("Log") && !it.className.contains("_log") }
            ?.className
            ?.substringAfterLast('.')
            ?: "UnknownClass"
    }


    when (level) {
        Log.VERBOSE -> Log.v(finalTag, content)
        Log.DEBUG   -> Log.d(finalTag, content)
        Log.INFO    -> Log.i(finalTag, content)
        Log.WARN    -> Log.w(finalTag, content)
        Log.ERROR   -> Log.e(finalTag, content)
        else       -> Log.d(finalTag, content)  // 默认使用 DEBUG
    }
}

