package com.pine.pinedroid.utils

fun List<*>.ToString(maxItems: Int): String {
    if (this.isEmpty()) return "Empty List"


    val items = this.take(maxItems).joinToString(",\n  ") { item ->
        when (item) {
            null -> "null"
            is String -> "\"$item\""
            is Number, is Boolean -> item.toString()
            is List<*> -> item.ToString(maxItems)
            is Map<*, *> -> item.ToString(maxItems)
            else -> gson.toJson(item)
        }
    }

    return if (this.size > maxItems) {
        "[\n  $items,\n  ... (${this.size - maxItems} more items)\n]"
    } else {
        "[\n  $items\n]"
    }
}