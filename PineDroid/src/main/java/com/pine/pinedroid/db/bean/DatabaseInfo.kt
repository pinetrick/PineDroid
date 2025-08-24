package com.pine.pinedroid.db.bean

// 数据库信息数据类
data class DatabaseInfo(
    val name: String,
    val path: String,
    val size: Long,
    val lastModified: Long
)