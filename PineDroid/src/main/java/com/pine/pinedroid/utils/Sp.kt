package com.pine.pinedroid.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

private const val NAME = "app_prefs"
val prefs: SharedPreferences by lazy {
    appContext.getSharedPreferences(NAME, Context.MODE_PRIVATE)
}

/**
 * 设置、获取或删除 SharedPreferences
 * 支持基本类型和对象序列化存储
 *
 * @param key 键
 * @param value 可选值：
 *  - null → 删除
 *  - 非 null → 保存
 *  - 不传 → 获取
 * @param clazz 可选，用于获取对象时反序列化
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T> sp(key: String, value: T? = null): T? {
    val typeKey = "${key}_type" // 保存类型的key

    return when {
        value == null -> { // 获取
            val type = prefs.getString(typeKey, null)
            when (type) {
                "String" -> prefs.getString(key, null) as T?
                "Int" -> prefs.getInt(key, 0) as T?
                "Long" -> prefs.getLong(key, 0L) as T?
                "Float" -> prefs.getFloat(key, 0f) as T?
                "Double" -> prefs.getFloat(key, 0f) as T?
                "Boolean" -> prefs.getBoolean(key, false) as T?
                "Object" -> prefs.getString(key, null)?.let { gson.fromJson(it, T::class.java) as T }
                else -> null
            }
        }
        value == "null" -> { // 删除
            prefs.edit {
                remove(key)
                remove(typeKey)
            }
            null
        }
        else -> { // 设置
            prefs.edit {
                when (value) {
                    is String -> {
                        putString(key, value)
                        putString(typeKey, "String")
                    }
                    is Int -> {
                        putInt(key, value)
                        putString(typeKey, "Int")
                    }
                    is Long -> {
                        putLong(key, value)
                        putString(typeKey, "Long")
                    }
                    is Float -> {
                        putFloat(key, value)
                        putString(typeKey, "Float")
                    }
                    is Boolean -> {
                        putBoolean(key, value)
                        putString(typeKey, "Boolean")
                    }
                    else -> {
                        putString(key, gson.toJson(value))
                        putString(typeKey, "Object")
                    }
                }
            }
            value
        }
    }
}
