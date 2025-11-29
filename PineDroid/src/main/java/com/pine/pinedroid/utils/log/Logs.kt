package com.pine.pinedroid.utils.log

import android.util.Log
import com.pine.pinedroid.PineConfig
import com.pine.pinedroid.isDebug
import com.pine.pinedroid.utils.ToString
import com.pine.pinedroid.utils.gson

fun <T> log(content: T?) = logd(content)
fun <T> log(key: String, content: T?, level: Int = Log.DEBUG) {
    if (!isDebug) {
        if (level <= Log.DEBUG) return
    }

    val output = try {
        when (content) {
            null -> "null"
            is String, is Number, is Boolean -> content.toString()
            is Exception -> (content.cause?.toString() ?: content.toString()).also { content.printStackTrace()  }
            is ByteArray -> content.ToString()
            is List<*> -> content.joinToString(limit = 30) { item ->
                when (item) {
                    null -> "null"
                    is String, is Number, is Boolean -> item.toString()
                    is Map<*, *> -> "Map(size=${item.size})"
                    is List<*> -> "List(size=${item.size})"
                    is ByteArray -> "ByteArray(size=${item.size})"
                    else -> item::class.java.simpleName
                }
            }
            is Map<*, *> -> content.entries.joinToString(limit = 30) { (key, value) ->
                val keyStr = when (key) {
                    null -> "null"
                    is String, is Number, is Boolean -> key.toString()
                    else -> key::class.java.simpleName
                }
                val valueStr = when (value) {
                    null -> "null"
                    is String, is Number, is Boolean -> value.toString()
                    is Map<*, *> -> "Map(size=${value.size})"
                    is List<*> -> "List(size=${value.size})"
                    is ByteArray -> "ByteArray(size=${value.size})"
                    else -> value::class.java.simpleName
                }
                "$keyStr: $valueStr"
            }
            is Class<*> -> "Class(${content.name})" // 专门处理 Class 类型
            else -> {
                // 安全地序列化，避免 Class 类型
                try {
                    gson.toJson(content)
                } catch (e: UnsupportedOperationException) {
                    // 如果序列化失败，返回类型信息
                    "${content::class.java.name}: ${content.toString().take(100)}"
                } catch (e: Exception) {
                    "Serialization error: ${e.message}"
                }
            }
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
    var finalTag = tag.ifEmpty {
        // 获取调用者的类名
        Thread.currentThread().stackTrace
            .firstOrNull { !it.className.contains("Log") && !it.className.contains("_log") }
            ?.className
            ?.substringAfterLast('.')
            ?: "UnknownClass"
    }
    finalTag = "Pine_$finalTag"


    when (level) {
        Log.VERBOSE -> Log.v(finalTag, content)
        Log.DEBUG   -> Log.d(finalTag, content)
        Log.INFO    -> Log.i(finalTag, content)
        Log.WARN    -> Log.w(finalTag, content)
        Log.ERROR   -> Log.e(finalTag, content)
        else       -> Log.d(finalTag, content)  // 默认使用 DEBUG
    }
}

