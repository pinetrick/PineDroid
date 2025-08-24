package com.pine.pinedroid.db.bean


// TableInfo 数据类
data class TableInfo(
    val name: String,
    val type: String, // "table", "view", etc.
    val sql: String? = null,
    val rowCount: Int? = null,
)

