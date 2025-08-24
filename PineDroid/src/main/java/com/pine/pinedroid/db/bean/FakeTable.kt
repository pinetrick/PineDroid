package com.pine.pinedroid.db.bean

import com.pine.pinedroid.db.ColumnInfo
import com.pine.pinedroid.db.DbRecord
import kotlin.Int

val fakeColumnInfos by lazy {
    listOf(
        ColumnInfo(
            cid = 1,
            name = "id",
            type = "INTEGER",
            notNull = false,
            defaultValue = null,
            isPrimaryKey = true,
            isAutoIncrement = true
        ),
        ColumnInfo(
            cid = 2,
            name = "user_id",
            type = "INTEGER",
            notNull = false,
            defaultValue = null,
            isPrimaryKey = false,
            isAutoIncrement = false
        ),
        ColumnInfo(
            cid = 1,
            name = "userName",
            type = "String",
            notNull = false,
            defaultValue = null,
            isPrimaryKey = false,
            isAutoIncrement = false
        ),
    )
}


val fakeDbRecords  by lazy {
    listOf(fakeDbRecord, fakeDbRecord, fakeDbRecord)
}

val fakeDbRecord by lazy {
    val r = DbRecord(
        tableName = "Test Table",
        dbName = "Test DB",
    )
    r["id"] = 1
    r["user"] = 1.0f
    r["name"] = "name"

    r
}