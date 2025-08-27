package com.pine.pinedroid.utils

fun Map<*, *>.ToString(maxEntries: Int = 30): String {
    if (this.isEmpty()) return "Empty Map"


    val entries = this.entries.take(maxEntries).joinToString(",\n  ") { (key, value) ->
        val keyStr = when (key) {
            null -> "null"
            is String -> "\"$key\""
            is Number, is Boolean -> key.toString()
            else -> gson.toJson(key)
        }

        val valueStr = when (value) {
            null -> "null"
            is String -> "\"$value\""
            is Number, is Boolean -> value.toString()
            is List<*> -> value.ToString(maxEntries)
            is Map<*, *> -> value.ToString(maxEntries)
            else -> gson.toJson(value)
        }

        "$keyStr: $valueStr"
    }

    return if (this.size > maxEntries) {
        "{\n  $entries,\n  ... (${this.size - maxEntries} more entries)\n}"
    } else {
        "{\n  $entries\n}"
    }
}