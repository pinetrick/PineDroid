package com.pine.pinedroid.db

import com.pine.pinedroid.utils.gson

class DbRecord(public var tableName: String, public var dbName: String) {
    var kvs: MutableMap<String, Any?> = mutableMapOf()
    var dirtyKeys: MutableSet<String> = mutableSetOf()

    private val dbConnection: DbConnection
        get() = db(dbName)

    operator fun get(column: String): Any? = kvs[column]

    operator fun set(column: String, value: Any?) {
        if (kvs[column] != value) {
            kvs[column] = value
            dirtyKeys.add(column) // 自动防重复
        }
    }

    fun save() {
        if (this["id"] == null) saveNew()
        else update()
    }

    /** 插入新记录 */
    private fun saveNew() {
        if (tableName.isBlank()) throw IllegalStateException("tableName 未设置")

        val cols = kvs.keys.joinToString(", ")
        val placeholders = kvs.keys.joinToString(", ") { "?" }
        val args = kvs.values.toTypedArray()



        val id = dbConnection.insert(tableName, null, kvs)
        this["id"] = id
    }

    /** 更新已有记录 */
    private fun update() {
        if (tableName.isBlank()) throw IllegalStateException("tableName 未设置")
        if (this["id"] == null) throw IllegalStateException("update 需要 id")
        if (dirtyKeys.isEmpty()) return

        val setSql = dirtyKeys.joinToString(", ") { "$it = ?" }

        val sql = "UPDATE $tableName SET $setSql WHERE id = ?"
        val args = dirtyKeys.map { kvs[it] }.toMutableList()
        args.add(this["id"]!!)

        dbConnection.execute(sql, args.toTypedArray())
        dirtyKeys.clear()
    }

    override fun toString(): String {
        return gson.toJson(this)
    }

}